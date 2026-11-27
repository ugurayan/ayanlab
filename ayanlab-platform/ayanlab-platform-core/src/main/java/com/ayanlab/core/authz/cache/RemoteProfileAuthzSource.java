package com.ayanlab.core.authz.cache;

import com.ayanlab.core.constant.ApiClientHeaders;
import com.ayanlab.core.constant.ApiPathPrefixes;
import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.dto.EffectiveAuthorization;
import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.core.exception.ForbiddenException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
@ConditionalOnProperty(prefix = "ayanlabs.authz-cache", name = "source", havingValue = "remote", matchIfMissing = true)
public class RemoteProfileAuthzSource implements ProfileAuthzSource {

    private static final Logger log = LoggerFactory.getLogger(RemoteProfileAuthzSource.class);
    private static final TypeReference<ApiResponse<EffectiveAuthorization>> EFFECTIVE_TYPE = new TypeReference<>() {
    };

    private final AuthzCacheProperties properties;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();

    public RemoteProfileAuthzSource(AuthzCacheProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    public EffectiveAuthorization load(CoreContext context) {
        if (context == null || context.getToken() == null || context.getToken().isBlank()) {
            throw new ForbiddenException("Platform authorization required");
        }
        try {
            String base = properties.getPlatformApiUrl() == null ? "" : properties.getPlatformApiUrl().replaceAll("/+$", "");
            String url = base + ApiPathPrefixes.AUTH + "/authorization/effective";
            HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(5))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + context.getToken())
                    .header(ApiClientHeaders.CLIENT_TOKEN, context.getToken())
                    .header(HttpHeaders.ACCEPT, "application/json")
                    .GET();
            if (context.getRefreshToken() != null && !context.getRefreshToken().isBlank()) {
                builder.header(ApiClientHeaders.CLIENT_REFRESH_TOKEN, context.getRefreshToken());
            }
            if (context.getCorrelationId() != null) {
                builder.header(ApiClientHeaders.CORRELATION_ID, context.getCorrelationId().toString());
            }
            if (context.getAppId() != null) {
                builder.header(ApiClientHeaders.APP_ID, context.getAppId().toString());
            }
            if (context.getProfileId() != null) {
                builder.header(ApiClientHeaders.PROFILE_ID, context.getProfileId().toString());
            }
            if (context.getOrgId() != null) {
                builder.header(ApiClientHeaders.ORG_ID, context.getOrgId().toString());
            }
            if (context.getClientType() != null) {
                builder.header(ApiClientHeaders.CLIENT_TYPE, context.getClientType().name());
            }
            HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() / 100 != 2) {
                log.warn("platform effective auth HTTP {}", response.statusCode());
                throw new ForbiddenException("Platform authorization check failed");
            }
            ApiResponse<EffectiveAuthorization> envelope = objectMapper.readValue(response.body(), EFFECTIVE_TYPE);
            if (envelope.getData() == null) {
                throw new ForbiddenException("Platform authorization payload missing");
            }
            return envelope.getData();
        } catch (ForbiddenException ex) {
            throw ex;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new ForbiddenException("Platform authorization interrupted");
        } catch (Exception ex) {
            log.warn("platform effective auth failed: {}", ex.toString());
            throw new ForbiddenException("Platform authorization unavailable");
        }
    }
}
