package com.ayanlabs.core.controller;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.core.entity.Role;
import com.ayanlabs.core.service.IRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/core/roles")
@RequiredArgsConstructor
public class RoleController extends BaseController {

    private final IRoleService roleService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Role>> create(
            @RequestBody Role role,
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
        log.info("[create-role] correlationId={} requestBody={}", correlationId, role);
        CoreContext context = buildContextFromHeaders(appId, clientIp, language, clientUid, clientPid, clientOid, refreshToken, token, clientType, correlationId);
        Role created = roleService.create(role, context);
        log.info("[create-role] correlationId={} createdRoleId={}", correlationId, created != null ? created.getId() : null);
        return ok(created, correlationId);
    }

    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Role>> update(
            @PathVariable UUID id,
            @RequestBody Role role,
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
        log.info("[update-role] correlationId={} id={} requestBody={}", correlationId, id, role);
        role.setId(id);
        CoreContext context = buildContextFromHeaders(appId, clientIp, language, clientUid, clientPid, clientOid, refreshToken, token, clientType, correlationId);
        Role updated = roleService.update(role, context);
        log.info("[update-role] correlationId={} updatedRoleId={}", correlationId, updated != null ? updated.getId() : null);
        return ok(updated, correlationId);
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Role>> getById(
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
        log.info("[get-role] correlationId={} id={}", correlationId, id);
        CoreContext context = buildContextFromHeaders(appId, clientIp, language, clientUid, clientPid, clientOid, refreshToken, token, clientType, correlationId);
        Role found = roleService.findById(id, context);
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
        log.info("[delete-role] correlationId={} id={}", correlationId, id);
        CoreContext context = buildContextFromHeaders(appId, clientIp, language, clientUid, clientPid, clientOid, refreshToken, token, clientType, correlationId);
        roleService.deleteById(id, context);
        return ok(null, correlationId);
    }
}
