package com.ayanlab.core.authz.catalog;

import com.ayanlab.core.helper.ApiClientHeaders;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

/**
 * Publishes this app's authorization catalog (roles, policy groups, permissions, seed users) to the platform
 * on every startup. The platform import is idempotent, so re-publishing is safe.
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "platform.authz.catalog.publish", name = "enabled", havingValue = "true")
public class AuthorizationCatalogPublisher {

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();

    @Value("${platform.authz.catalog.publish.locations:" + AuthorizationCatalogs.DEFAULT_LOCATION + "}")
    private String locations;

    @Value("${platform.authz.catalog.publish.max-attempts:20}")
    private int maxAttempts;

    @Value("${platform.authz.catalog.publish.initial-backoff-ms:2000}")
    private long initialBackoffMs;

    @Value("${platform.api-url:http://localhost:8102}")
    private String platformApiUrl;

    @Value("${platform.internal-token:}")
    private String internalToken;

    @Value("${platform.app-id:}")
    private String appId;

    public AuthorizationCatalogPublisher(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        Thread.ofVirtual().name("authz-catalog-publisher").start(this::publishWithRetry);
    }

    void publishWithRetry() {
        if (internalToken == null || internalToken.isBlank()) {
            log.warn("[AuthorizationCatalogPublisher] platform.internal-token is empty; catalog not published");
            return;
        }
        AppAuthorizationCatalog catalog;
        try {
            catalog = loadCatalog();
        } catch (Exception e) {
            log.error("[AuthorizationCatalogPublisher] Could not load catalog from {}", locations, e);
            return;
        }
        List<String> errors = catalog.validate();
        if (!errors.isEmpty()) {
            log.error("[AuthorizationCatalogPublisher] Catalog for '{}' is invalid, not publishing: {}", catalog.appCode(), errors);
            return;
        }
        long backoff = initialBackoffMs;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                HttpResponse<String> response = post(catalog);
                int status = response.statusCode();
                if (status / 100 == 2) {
                    log.info("[AuthorizationCatalogPublisher] Published catalog '{}': {}", catalog.appCode(), response.body());
                    return;
                }
                if (status == 400 || status == 401 || status == 403) {
                    log.error("[AuthorizationCatalogPublisher] Platform rejected catalog '{}' (HTTP {}): {}", catalog.appCode(), status, response.body());
                    return;
                }
                log.warn("[AuthorizationCatalogPublisher] Attempt {}/{} failed (HTTP {}), retrying in {} ms", attempt, maxAttempts, status, backoff);
            } catch (IOException e) {
                log.warn("[AuthorizationCatalogPublisher] Attempt {}/{} failed ({}), retrying in {} ms", attempt, maxAttempts, e.getMessage(), backoff);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            try {
                Thread.sleep(backoff);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            backoff = Math.min(backoff * 2, 60_000);
        }
        log.error("[AuthorizationCatalogPublisher] Giving up publishing catalog '{}' after {} attempts", catalog.appCode(), maxAttempts);
    }

    AppAuthorizationCatalog loadCatalog() throws IOException {
        return AuthorizationCatalogs.load(locations, objectMapper);
    }

    private HttpResponse<String> post(AppAuthorizationCatalog catalog) throws IOException, InterruptedException {
        HttpRequest.Builder request = HttpRequest.newBuilder()
            .uri(URI.create(platformApiUrl.replaceAll("/+$", "") + InternalApiHeaders.CATALOG_PATH))
            .timeout(Duration.ofSeconds(60))
            .header("Content-Type", "application/json")
            .header(InternalApiHeaders.INTERNAL_TOKEN, internalToken)
            .header(ApiClientHeaders.CORRELATION_ID, UUID.randomUUID().toString())
            .header(ApiClientHeaders.CLIENT_TYPE, "API")
            .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(catalog)));
        if (appId != null && !appId.isBlank()) {
            request.header(ApiClientHeaders.APP_ID, appId);
        }
        return httpClient.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }
}
