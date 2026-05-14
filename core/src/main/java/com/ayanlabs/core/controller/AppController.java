package com.ayanlabs.core.controller;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.core.data.responses.PagedResponse;
import com.ayanlabs.core.entity.App;
import com.ayanlabs.core.service.IAppService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/core/apps")
@RequiredArgsConstructor
public class AppController extends BaseController {

    private final IAppService appService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<App>> create(
            @Valid @RequestBody App app,
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
        log.info("[create-app] correlationId={} requestBody={}", correlationId, app);
        CoreContext context = buildContextFromHeaders(appId, clientIp, language, clientUid, clientPid, clientOid, refreshToken, token, clientType, correlationId);
        App created = appService.create(app, context);
        log.info("[create-app] correlationId={} createdAppId={}", correlationId, created != null ? created.getId() : null);
        return ok(created, correlationId);
    }

    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<App>> update(
            @PathVariable UUID id,
            @Valid @RequestBody App app,
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
        log.info("[update-app] correlationId={} id={} requestBody={}", correlationId, id, app);
        app.setId(id);
        CoreContext context = buildContextFromHeaders(appId, clientIp, language, clientUid, clientPid, clientOid, refreshToken, token, clientType, correlationId);
        App updated = appService.update(app, context);
        log.info("[update-app] correlationId={} updatedAppId={}", correlationId, updated != null ? updated.getId() : null);
        return ok(updated, correlationId);
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<App>> getById(
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
        log.info("[get-app] correlationId={} id={}", correlationId, id);
        CoreContext context = buildContextFromHeaders(appId, clientIp, language, clientUid, clientPid, clientOid, refreshToken, token, clientType, correlationId);
        App found = appService.findById(id, context);
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
        log.info("[delete-app] correlationId={} id={}", correlationId, id);
        CoreContext context = buildContextFromHeaders(appId, clientIp, language, clientUid, clientPid, clientOid, refreshToken, token, clientType, correlationId);
        appService.deleteById(id, context);
        return ok(null, correlationId);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<PagedResponse<App>>> list(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "20") int size,
            @RequestParam(value = "appId", required = false) UUID appIdParam,
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "name", required = false) String name,
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
        log.info("[list-apps] correlationId={} page={} size={} appId={} code={} name={}", correlationId, page, size, appIdParam, code, name);
        CoreContext context = buildContextFromHeaders(appId, clientIp, language, clientUid, clientPid, clientOid, refreshToken, token, clientType, correlationId);
        PagedResponse<App> paged = appService.findAll(page, size, appIdParam, code, name, context);
        return ok(paged, correlationId);
    }
}
