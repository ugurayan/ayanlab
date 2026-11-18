package com.ayanlab.security.authz;

import com.ayanlab.core.authz.catalog.InternalApiHeaders;
import com.ayanlab.core.helper.ApiClientHeaders;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * App-backend checker: asks the platform for the current authz version and caches it briefly.
 * If the platform cannot be reached the token is accepted (it is still signed and expires normally).
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "platform.authz.version-check.mode", havingValue = "remote")
public class RemoteAuthzVersionChecker implements AuthzVersionChecker {

    private record Cached(String version, Instant expiresAt) {}

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();
    private final Map<String, Cached> cache = new ConcurrentHashMap<>();

    @Value("${platform.api-url:http://localhost:8102}")
    private String platformApiUrl;

    @Value("${platform.internal-token:}")
    private String internalToken;

    @Value("${platform.authz.version-check.cache-seconds:30}")
    private long cacheSeconds;

    public RemoteAuthzVersionChecker(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String currentVersion(String appCode, UUID profileId) {
        String key = appCode + "|" + profileId;
        Cached cached = cache.get(key);
        Instant now = Instant.now();
        if (cached != null && cached.expiresAt().isAfter(now)) {
            return cached.version();
        }
        String version = fetch(appCode, profileId);
        if (version != null) {
            cache.put(key, new Cached(version, now.plusSeconds(cacheSeconds)));
        }
        return version;
    }

    private String fetch(String appCode, UUID profileId) {
        if (internalToken == null || internalToken.isBlank()) {
            return null;
        }
        try {
            String url = platformApiUrl.replaceAll("/+$", "") + InternalApiHeaders.AUTHZ_VERSION_PATH
                + "?appCode=" + URLEncoder.encode(appCode, StandardCharsets.UTF_8) + "&profileId=" + profileId;
            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(3))
                .header(InternalApiHeaders.INTERNAL_TOKEN, internalToken)
                .header(ApiClientHeaders.CORRELATION_ID, UUID.randomUUID().toString())
                .header(ApiClientHeaders.CLIENT_TYPE, "API")
                .GET()
                .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() / 100 != 2) {
                log.warn("[RemoteAuthzVersionChecker] Platform returned HTTP {} for {}", response.statusCode(), appCode);
                return null;
            }
            JsonNode version = objectMapper.readTree(response.body()).path("data").path("version");
            return version.isTextual() ? version.asText() : null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (Exception e) {
            log.warn("[RemoteAuthzVersionChecker] Version lookup failed: {}", e.getMessage());
            return null;
        }
    }
}
