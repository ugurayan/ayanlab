package com.ayanlabs.core.controller;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.core.data.responses.PagedResponse;
import com.ayanlabs.core.entity.NotificationTemplate;
import com.ayanlabs.core.data.enums.NotificationTypeEnum;
import com.ayanlabs.core.service.INotificationTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/core/templates")
@RequiredArgsConstructor
public class NotificationTemplateController extends BaseController {

    private final INotificationTemplateService templateService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<NotificationTemplate>> create(
            @Valid @RequestBody NotificationTemplate template,
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationId,
            @RequestHeader(value = "X-App-Id", required = false) String appId,
            @RequestHeader(value = "X-Client-Ip", required = false) String clientIp,
            @RequestHeader(value = "X-Client-Language", required = false) String language,
            @RequestHeader(value = "X-Client-UID", required = false) String clientUid,
            @RequestHeader(value = "X-Client-PID", required = false) String clientPid,
            @RequestHeader(value = "X-Client-OID", required = false) String clientOid,
            @RequestHeader(value = "X-Client-Refresh-Token", required = false) String refreshToken,
            @RequestHeader(value = "X-Client-Token", required = false) String token,
            @RequestHeader(value = "X-Client-Type", required = false) String clientType
    ) {
        if (!org.springframework.util.StringUtils.hasText(correlationId)) correlationId = UUID.randomUUID().toString();
        log.info("[create-template] correlationId={} requestBody={}", correlationId, template);
        CoreContext context = buildContextFromHeaders(appId, clientIp, language, clientUid, clientPid, clientOid, refreshToken, token, clientType, correlationId);
        NotificationTemplate created = templateService.create(template, context);
        log.info("[create-template] correlationId={} createdId={}", correlationId, created != null ? created.getId() : null);
        return ok(created, correlationId);
    }

    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<NotificationTemplate>> update(
            @PathVariable UUID id,
            @Valid @RequestBody NotificationTemplate template,
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationId,
            @RequestHeader(value = "X-App-Id", required = false) String appId,
            @RequestHeader(value = "X-Client-Ip", required = false) String clientIp,
            @RequestHeader(value = "X-Client-Language", required = false) String language,
            @RequestHeader(value = "X-Client-UID", required = false) String clientUid,
            @RequestHeader(value = "X-Client-PID", required = false) String clientPid,
            @RequestHeader(value = "X-Client-OID", required = false) String clientOid,
            @RequestHeader(value = "X-Client-Refresh-Token", required = false) String refreshToken,
            @RequestHeader(value = "X-Client-Token", required = false) String token,
            @RequestHeader(value = "X-Client-Type", required = false) String clientType
    ) {
        if (!org.springframework.util.StringUtils.hasText(correlationId)) correlationId = UUID.randomUUID().toString();
        template.setId(id);
        log.info("[update-template] correlationId={} id={} requestBody={}", correlationId, id, template);
        CoreContext context = buildContextFromHeaders(appId, clientIp, language, clientUid, clientPid, clientOid, refreshToken, token, clientType, correlationId);
        NotificationTemplate updated = templateService.update(template, context);
        log.info("[update-template] correlationId={} updatedId={}", correlationId, updated != null ? updated.getId() : null);
        return ok(updated, correlationId);
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<NotificationTemplate>> getById(
            @PathVariable UUID id,
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationId,
            @RequestHeader(value = "X-App-Id", required = false) String appId,
            @RequestHeader(value = "X-Client-Ip", required = false) String clientIp,
            @RequestHeader(value = "X-Client-Language", required = false) String language,
            @RequestHeader(value = "X-Client-UID", required = false) String clientUid,
            @RequestHeader(value = "X-Client-PID", required = false) String clientPid,
            @RequestHeader(value = "X-Client-OID", required = false) String clientOid,
            @RequestHeader(value = "X-Client-Refresh-Token", required = false) String refreshToken,
            @RequestHeader(value = "X-Client-Token", required = false) String token,
            @RequestHeader(value = "X-Client-Type", required = false) String clientType
    ) {
        if (!org.springframework.util.StringUtils.hasText(correlationId)) correlationId = UUID.randomUUID().toString();
        log.info("[get-template] correlationId={} id={}", correlationId, id);
        CoreContext context = buildContextFromHeaders(appId, clientIp, language, clientUid, clientPid, clientOid, refreshToken, token, clientType, correlationId);
        NotificationTemplate found = templateService.findById(id, context);
        return ok(found, correlationId);
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<ApiResponse<Object>> deleteById(
            @PathVariable UUID id,
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationId,
            @RequestHeader(value = "X-App-Id", required = false) String appId,
            @RequestHeader(value = "X-Client-Ip", required = false) String clientIp,
            @RequestHeader(value = "X-Client-Language", required = false) String language,
            @RequestHeader(value = "X-Client-UID", required = false) String clientUid,
            @RequestHeader(value = "X-Client-PID", required = false) String clientPid,
            @RequestHeader(value = "X-Client-OID", required = false) String clientOid,
            @RequestHeader(value = "X-Client-Refresh-Token", required = false) String refreshToken,
            @RequestHeader(value = "X-Client-Token", required = false) String token,
            @RequestHeader(value = "X-Client-Type", required = false) String clientType
    ) {
        if (!org.springframework.util.StringUtils.hasText(correlationId)) correlationId = UUID.randomUUID().toString();
        log.info("[delete-template] correlationId={} id={}", correlationId, id);
        CoreContext context = buildContextFromHeaders(appId, clientIp, language, clientUid, clientPid, clientOid, refreshToken, token, clientType, correlationId);
        templateService.deleteById(id, context);
        return ok(null, correlationId);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<PagedResponse<NotificationTemplate>>> list(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "20") int size,
            @RequestParam(value = "appId", required = false) UUID appIdParam,
            @RequestParam(value = "type", required = false) NotificationTypeEnum type,
            @RequestParam(value = "code", required = false) String code,
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationId,
            @RequestHeader(value = "X-App-Id", required = false) String appId,
            @RequestHeader(value = "X-Client-Ip", required = false) String clientIp,
            @RequestHeader(value = "X-Client-Language", required = false) String language,
            @RequestHeader(value = "X-Client-UID", required = false) String clientUid,
            @RequestHeader(value = "X-Client-PID", required = false) String clientPid,
            @RequestHeader(value = "X-Client-OID", required = false) String clientOid,
            @RequestHeader(value = "X-Client-Refresh-Token", required = false) String refreshToken,
            @RequestHeader(value = "X-Client-Token", required = false) String token,
            @RequestHeader(value = "X-Client-Type", required = false) String clientType
    ) {
        if (!org.springframework.util.StringUtils.hasText(correlationId)) correlationId = UUID.randomUUID().toString();
        log.info("[list-templates] correlationId={} page={} size={} appId={} type={} code={}", correlationId, page, size, appIdParam, type, code);
        CoreContext context = buildContextFromHeaders(appId, clientIp, language, clientUid, clientPid, clientOid, refreshToken, token, clientType, correlationId);
        PagedResponse<NotificationTemplate> paged = templateService.findAll(page, size, appIdParam, type, code, context);
        return ok(paged, correlationId);
    }
}

