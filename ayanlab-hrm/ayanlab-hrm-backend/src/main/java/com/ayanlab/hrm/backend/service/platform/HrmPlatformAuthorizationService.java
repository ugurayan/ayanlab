package com.ayanlab.hrm.backend.service.platform;

import com.ayanlab.core.data.CoreContext;
import com.ayanlab.core.data.dto.EffectiveAuthorization;
import com.ayanlab.core.data.responses.ApiResponse;
import com.ayanlab.core.exception.ForbiddenException;
import com.ayanlab.core.helper.ApiClientHeaders;
import com.ayanlab.core.util.PermissionCodes;
import com.ayanlab.core.web.ApiPathPrefixes;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

/**
 * Backend-to-backend authorization: re-resolves effective permissions from the platform API
 * using the caller's bearer token before HRM domain mutations and reads.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HrmPlatformAuthorizationService {

    private static final TypeReference<ApiResponse<EffectiveAuthorization>> EFFECTIVE_TYPE =
            new TypeReference<>() {};

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient =
            HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();

    @Value("${platform.api-url:http://localhost:8102}")
    private String platformApiUrl;

    @Value("${hrm.platform-auth.enabled:true}")
    private boolean enabled;

  public void requirePermission(CoreContext context, String requiredPermission) {
        if (!enabled) {
            return;
        }
        if (context == null || context.getToken() == null || context.getToken().isBlank()) {
            throw new ForbiddenException("Platform authorization required");
        }
        EffectiveAuthorization effective = fetchEffective(context);
        Set<String> granted = effective.getPermissionCodes() != null ? effective.getPermissionCodes() : Set.of();
        if (!PermissionCodes.anyImplies(granted, requiredPermission)) {
            throw new ForbiddenException("Platform denied permission: " + requiredPermission);
        }
    }

    private EffectiveAuthorization fetchEffective(CoreContext context) {
        try {
            String base = platformApiUrl.replaceAll("/+$", "");
            String url = base + ApiPathPrefixes.AUTH + "/authorization/effective";
            HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(5))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + context.getToken())
                    .header(HttpHeaders.ACCEPT, "application/json")
                    .GET();
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
                log.warn("[HrmPlatformAuthorization] HTTP {} from platform effective auth", response.statusCode());
                throw new ForbiddenException("Platform authorization check failed");
            }
            ApiResponse<EffectiveAuthorization> envelope =
                    objectMapper.readValue(response.body(), EFFECTIVE_TYPE);
            if (envelope.getData() == null) {
                throw new ForbiddenException("Platform authorization payload missing");
            }
            return envelope.getData();
        } catch (ForbiddenException e) {
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ForbiddenException("Platform authorization interrupted");
        } catch (Exception e) {
            log.warn("[HrmPlatformAuthorization] {}", e.getMessage());
            throw new ForbiddenException("Platform authorization unavailable");
        }
    }
}