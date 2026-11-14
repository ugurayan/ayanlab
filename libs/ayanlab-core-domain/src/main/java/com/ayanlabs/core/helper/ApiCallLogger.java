package com.ayanlabs.core.helper;

import com.ayanlabs.core.data.CoreContext;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Structured request / response logging for controller endpoints (pilot).
 * {@link ClientHeaderSnapshot} is built from {@link CoreContext} after {@code buildContextFromHeaders}.
 */
public final class ApiCallLogger {

    private ApiCallLogger() {
    }

    public static String correlationId(CoreContext context) {
        if (context == null || context.getCorrelationId() == null) {
            return null;
        }
        return context.getCorrelationId().toString();
    }

    public static ClientHeaderSnapshot fromContext(CoreContext context) {
        if (context == null) {
            return new ClientHeaderSnapshot(null, null, null, null, null, null, null, null, null, null);
        }
        return new ClientHeaderSnapshot(
                uuidToString(context.getCorrelationId()),
                uuidToString(context.getAppId()),
                context.getClientIp(),
                context.getLanguage(),
                uuidToString(context.getUserId()),
                uuidToString(context.getProfileId()),
                uuidToString(context.getOrgId()),
                maskSecret(context.getRefreshToken()),
                maskSecret(context.getToken()),
                context.getClientType() != null ? context.getClientType().name() : null);
    }

    public static ApiCallTrace start(
            Logger log,
            String operation,
            String httpMethod,
            String path,
            CoreContext context,
            Object requestBody,
            Map<String, Object> pathAndQueryParams) {
        ClientHeaderSnapshot headers = fromContext(context);
        Instant timestamp = Instant.now();
        log.info(
                "[API_REQUEST] timestamp={} operation={} method={} path={} headers={} params={} body={}",
                timestamp,
                operation,
                httpMethod,
                path,
                headers,
                pathAndQueryParams == null || pathAndQueryParams.isEmpty() ? "{}" : pathAndQueryParams,
                requestBody);
        return new ApiCallTrace(log, operation, correlationId(context), timestamp);
    }

    public static String maskSecret(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        if (value.length() <= 8) {
            return "***";
        }
        return value.substring(0, 4) + "…" + value.substring(value.length() - 4);
    }

    private static String uuidToString(UUID id) {
        return id != null ? id.toString() : null;
    }

    public record ClientHeaderSnapshot(
            String correlationId,
            String appId,
            String clientIp,
            String language,
            String clientUid,
            String clientPid,
            String clientOid,
            String refreshToken,
            String token,
            String clientType) {
        @Override
        public String toString() {
            Map<String, String> map = new LinkedHashMap<>();
            put(map, "correlationId", correlationId);
            put(map, "appId", appId);
            put(map, "clientIp", clientIp);
            put(map, "language", language);
            put(map, "clientUid", clientUid);
            put(map, "clientPid", clientPid);
            put(map, "clientOid", clientOid);
            put(map, "refreshToken", refreshToken);
            put(map, "token", token);
            put(map, "clientType", clientType);
            return map.entrySet().stream()
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .collect(Collectors.joining(", ", "{", "}"));
        }

        private static void put(Map<String, String> map, String key, String value) {
            if (StringUtils.hasText(value)) {
                map.put(key, value);
            }
        }
    }

    public static final class ApiCallTrace {
        private final Logger log;
        private final String operation;
        private final String correlationId;
        private final Instant requestTimestamp;

        private ApiCallTrace(Logger log, String operation, String correlationId, Instant requestTimestamp) {
            this.log = log;
            this.operation = operation;
            this.correlationId = correlationId;
            this.requestTimestamp = requestTimestamp;
        }

        public void success(Object resultSummary) {
            Instant responseTimestamp = Instant.now();
            long durationMs = responseTimestamp.toEpochMilli() - requestTimestamp.toEpochMilli();
            log.info(
                    "[API_SUCCESS] requestTimestamp={} responseTimestamp={} durationMs={} operation={} correlationId={} result={}",
                    requestTimestamp,
                    responseTimestamp,
                    durationMs,
                    operation,
                    correlationId,
                    resultSummary);
        }

        public void successResponse(ResponseEntity<?> response) {
            Object summary = response.getStatusCode().value() + " " + response.getStatusCode();
            if (response.getBody() != null) {
                summary = summary + " body=" + response.getBody();
            }
            success(summary);
        }

        public void error(Exception exception) {
            Instant responseTimestamp = Instant.now();
            long durationMs = responseTimestamp.toEpochMilli() - requestTimestamp.toEpochMilli();
            log.error(
                    "[API_ERROR] requestTimestamp={} responseTimestamp={} durationMs={} operation={} correlationId={} errorType={} errorMessage={}",
                    requestTimestamp,
                    responseTimestamp,
                    durationMs,
                    operation,
                    correlationId,
                    exception.getClass().getSimpleName(),
                    exception.getMessage(),
                    exception);
        }
    }
}
