package com.ayanlabs.core.controller;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.core.data.responses.PagedResponse;
import com.ayanlabs.core.entity.Organization;
import com.ayanlabs.core.service.IOrganizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/core/organizations")
@RequiredArgsConstructor
public class OrganizationController extends BaseController {

    private final IOrganizationService organizationService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Organization>> create(
            @Valid @RequestBody Organization organization,
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
        log.info("[create-organization] correlationId={} requestBody={}", correlationId, organization);
        CoreContext context = buildContextFromHeaders(appId, clientIp, language, clientUid, clientPid, clientOid, refreshToken, token, clientType, correlationId);
        Organization created = organizationService.create(organization, context);
        log.info("[create-organization] correlationId={} createdOrganizationId={}", correlationId, created != null ? created.getId() : null);
        return ok(created, correlationId);
    }

    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Organization>> update(
            @PathVariable UUID id,
            @Valid @RequestBody Organization organization,
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
        log.info("[update-organization] correlationId={} id={} requestBody={}", correlationId, id, organization);
        organization.setId(id);
        CoreContext context = buildContextFromHeaders(appId, clientIp, language, clientUid, clientPid, clientOid, refreshToken, token, clientType, correlationId);
        Organization updated = organizationService.update(organization, context);
        log.info("[update-organization] correlationId={} updatedOrganizationId={}", correlationId, updated != null ? updated.getId() : null);
        return ok(updated, correlationId);
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Organization>> getById(
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
        log.info("[get-organization] correlationId={} id={}", correlationId, id);
        CoreContext context = buildContextFromHeaders(appId, clientIp, language, clientUid, clientPid, clientOid, refreshToken, token, clientType, correlationId);
        Organization found = organizationService.findById(id, context);
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
        log.info("[delete-organization] correlationId={} id={}", correlationId, id);
        CoreContext context = buildContextFromHeaders(appId, clientIp, language, clientUid, clientPid, clientOid, refreshToken, token, clientType, correlationId);
        organizationService.deleteById(id, context);
        return ok(null, correlationId);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<PagedResponse<Organization>>> list(
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
        log.info("[list-organizations] correlationId={} page={} size={} appId={} code={} name={}", correlationId, page, size, appIdParam, code, name);
        CoreContext context = buildContextFromHeaders(appId, clientIp, language, clientUid, clientPid, clientOid, refreshToken, token, clientType, correlationId);
        PagedResponse<Organization> paged = organizationService.findAll(page, size, appIdParam, code, name, context);
        return ok(paged, correlationId);
    }
}

