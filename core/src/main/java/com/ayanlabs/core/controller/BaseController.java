package com.ayanlabs.core.controller;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@Slf4j
public class BaseController {

    protected CoreContext buildContextFromHeaders(
            @RequestHeader(value = "X-App-Id", required = false) String appId,
            @RequestHeader(value = "X-Client-Ip", required = false) String clientIp,
            @RequestHeader(value = "X-Client-Language", required = false) String language,
            @RequestHeader(value = "X-Client-UID", required = false) String clientUid,
            @RequestHeader(value = "X-Client-PID", required = false) String clientPid,
            @RequestHeader(value = "X-Client-OID", required = false) String clientOid,
            @RequestHeader(value = "X-Client-Refresh-Token", required = false) String refreshToken,
            @RequestHeader(value = "X-Client-Token", required = false) String token,
            @RequestHeader(value = "X-Client-Type", required = false) String clientType,
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationId
    ) {
        CoreContext.CoreContextBuilder builder = CoreContext.builder();
        try {
            if (StringUtils.hasText(appId)) {
                builder.appId(UUID.fromString(appId));
            }
        } catch (Exception e) {
            log.warn("Invalid X-App-Id header: {}", appId);
        }
        // map tokens
        if (StringUtils.hasText(token)) builder.token(token);
        if (StringUtils.hasText(refreshToken)) builder.refreshToken(refreshToken);
        // client ids mapping to user/profile/org id if present
        try {
            if (StringUtils.hasText(clientUid)) builder.userId(UUID.fromString(clientUid));
        } catch (Exception e) {
            log.debug("Invalid X-Client-UID: {}", clientUid);
        }
        try {
            if (StringUtils.hasText(clientPid)) builder.profileId(UUID.fromString(clientPid));
        } catch (Exception e) {
            log.debug("Invalid X-Client-PID: {}", clientPid);
        }
        try {
            if (StringUtils.hasText(clientOid)) builder.orgId(UUID.fromString(clientOid));
        } catch (Exception e) {
            log.debug("Invalid X-Client-OID: {}", clientOid);
        }
        // map correlation id into context when provided
        try {
            if (StringUtils.hasText(correlationId)) builder.correlationId(UUID.fromString(correlationId));
        } catch (Exception e) {
            log.debug("Invalid X-Correlation-Id: {}", correlationId);
        }
        CoreContext context = builder.build();
        log.debug("Built CoreContext: appId={}, userId={}, profileId={}, orgId={}, correlationId={}",
                context.getAppId(), context.getUserId(), context.getProfileId(), context.getOrgId(), correlationId);
        return context;
    }

    protected <T> ResponseEntity<ApiResponse<T>> ok(T data, String correlationId) {
        ApiResponse<T> resp = ApiResponse.ok(data, null, correlationId);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(resp);
    }

    protected <T> ResponseEntity<ApiResponse<T>> error(String message, String correlationId) {
        ApiResponse<T> resp = ApiResponse.error(message, null, correlationId);
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(resp);
    }
}
