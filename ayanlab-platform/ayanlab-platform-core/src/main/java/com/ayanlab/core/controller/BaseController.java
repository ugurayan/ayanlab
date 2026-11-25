package com.ayanlab.core.controller;

import com.ayanlab.core.dto.ClientHeadersPayload;
import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.enums.ClientTypeEnum;
import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.core.util.ApiCallLogger;
import com.ayanlab.core.provider.AppSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.UUID;

public class BaseController {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    public record TracedCall(CoreContext context, ApiCallLogger.ApiCallTrace trace) {
    }

    protected CoreContext buildContextFromHeaders(ClientHeadersPayload headers) {
        CoreContext.CoreContextBuilder builder = CoreContext.builder();
        try {
            if (StringUtils.hasText(headers.appId())) {
                builder.appId(UUID.fromString(headers.appId()));
            }
        } catch (Exception e) {
            log.warn("Invalid X-App-Id header: {}", headers.appId());
        }
        if (StringUtils.hasText(headers.language())) {
            builder.language(headers.language());
        }
        if (StringUtils.hasText(headers.clientType())) {
            builder.clientType(parseClientType(headers.clientType()));
        }
        if (StringUtils.hasText(headers.token())) {
            builder.token(headers.token());
        }
        if (StringUtils.hasText(headers.refreshToken())) {
            builder.refreshToken(headers.refreshToken());
        }
        try {
            if (StringUtils.hasText(headers.correlationId())) {
                builder.correlationId(UUID.fromString(headers.correlationId()));
            }
        } catch (Exception e) {
            log.debug("Invalid X-Correlation-Id: {}", headers.correlationId());
        }
        try {
            if (StringUtils.hasText(headers.profileId())) {
                builder.profileId(UUID.fromString(headers.profileId()));
            }
        } catch (Exception e) {
            log.warn("Invalid X-Profile-Id header: {}", headers.profileId());
        }
        try {
            if (StringUtils.hasText(headers.orgId())) {
                builder.orgId(UUID.fromString(headers.orgId()));
            }
        } catch (Exception e) {
            log.warn("Invalid X-Org-Id header: {}", headers.orgId());
        }

        AppSecurityContext.AppContext security = AppSecurityContext.get();
        if (security != null) {
            if (security.getUserId() != null) {
                builder.userId(security.getUserId());
            }
            if (security.getProfileId() != null) {
                builder.profileId(security.getProfileId());
            }
            if (security.getOrgId() != null) {
                builder.orgId(security.getOrgId());
            }
        }

        CoreContext context = builder.build();
        log.debug("Built CoreContext: appId={}, userId={}, profileId={}, orgId={}, correlationId={}",
                context.getAppId(), context.getUserId(), context.getProfileId(), context.getOrgId(),
                headers.correlationId());
        return context;
    }

    protected String correlationIdFrom(CoreContext context) {
        return ApiCallLogger.correlationId(context);
    }

    protected ApiCallLogger.ApiCallTrace startApiCall(
            org.slf4j.Logger log,
            String operation,
            String httpMethod,
            String path,
            CoreContext context,
            Object requestBody,
            Map<String, Object> pathAndQueryParams) {
        return ApiCallLogger.start(log, operation, httpMethod, path, context, requestBody, pathAndQueryParams);
    }

    protected TracedCall tracedCall(
            org.slf4j.Logger log,
            String operation,
            String httpMethod,
            String path,
            ClientHeadersPayload headers,
            Object requestBody,
            Map<String, Object> pathAndQueryParams) {
        CoreContext context = buildContextFromHeaders(headers);
        ApiCallLogger.ApiCallTrace trace = startApiCall(
                log, operation, httpMethod, path, context, requestBody, pathAndQueryParams);
        return new TracedCall(context, trace);
    }

    protected <T> ResponseEntity<ApiResponse<T>> ok(T data, CoreContext context) {
        return ok(data, correlationIdFrom(context));
    }

    protected <T> ResponseEntity<ApiResponse<T>> ok(T data, String correlationId) {
        ApiResponse<T> resp = ApiResponse.ok(data, null, correlationId);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(resp);
    }

    protected <T> ResponseEntity<ApiResponse<T>> okBody(T data, CoreContext context) {
        return ResponseEntity.ok(ApiResponse.ok(data, null, correlationIdFrom(context)));
    }

    protected <T> ResponseEntity<ApiResponse<T>> okBody(T data, String message, CoreContext context) {
        return ResponseEntity.ok(ApiResponse.ok(data, message, correlationIdFrom(context)));
    }

    protected <T> ResponseEntity<ApiResponse<T>> createdBody(T data, String message, CoreContext context) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(data, message, correlationIdFrom(context)));
    }

    protected <T> ResponseEntity<ApiResponse<T>> error(String message, String correlationId) {
        ApiResponse<T> resp = ApiResponse.error(message, null, correlationId);
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(resp);
    }

    private ClientTypeEnum parseClientType(String clientType) {
        for (ClientTypeEnum value : ClientTypeEnum.values()) {
            if (value.name().equalsIgnoreCase(clientType)) {
                return value;
            }
        }
        log.debug("Unknown X-Client-Type header: {}", clientType);
        return ClientTypeEnum.Web;
    }
}
