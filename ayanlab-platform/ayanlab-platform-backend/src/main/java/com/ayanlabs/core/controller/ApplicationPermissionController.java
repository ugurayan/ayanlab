package com.ayanlabs.core.controller;

import com.ayanlabs.core.condition.AuthPlatformApi;
import com.ayanlabs.core.web.ApiPathPrefixes;
import com.ayanlabs.core.annotation.ClientHeaders;
import com.ayanlabs.core.data.ClientHeadersPayload;
import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.core.entity.ApplicationPermission;
import com.ayanlabs.core.service.IApplicationPermissionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@AuthPlatformApi
@RestController
@RequestMapping(ApiPathPrefixes.AUTH + "/application-permissions")
@Tag(name = "Auth · ApplicationPermissionController", description = "App to permission mappings")
@RequiredArgsConstructor
public class ApplicationPermissionController extends BaseController {

    private static final String BASE_PATH = ApiPathPrefixes.AUTH + "/application-permissions";
    private final IApplicationPermissionService applicationPermissionService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ApplicationPermission>> create(
            @RequestBody ApplicationPermission item,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "create-application-permission", "POST", BASE_PATH, headers, item, null);
        try {
            ApplicationPermission created = applicationPermissionService.create(item, call.context());
            call.trace().success(Map.of("createdId", created.getId(), "success", true));
            return ok(created, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ApplicationPermission>> update(
            @PathVariable UUID id,
            @RequestBody ApplicationPermission item,
            @ClientHeaders ClientHeadersPayload headers) {
        item.setId(id);
        TracedCall call = tracedCall(log, "update-application-permission", "PUT", BASE_PATH + "/{id}", headers, item, Map.of("id", id));
        try {
            return ok(applicationPermissionService.update(item, call.context()), call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ApplicationPermission>> getById(
            @PathVariable UUID id,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "get-application-permission", "GET", BASE_PATH + "/{id}", headers, null, Map.of("id", id));
        try {
            return ok(applicationPermissionService.findById(id, call.context()), call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<ApplicationPermission>>> list(
            @RequestParam(required = false) UUID appId,
            @RequestParam(required = false) Boolean includeInactive,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("appId", appId);
        params.put("includeInactive", includeInactive);
        TracedCall call = tracedCall(log, "list-application-permissions", "GET", BASE_PATH, headers, null, params);
        try {
            List<ApplicationPermission> data =
                    applicationPermissionService.list(appId, Boolean.TRUE.equals(includeInactive), call.context());
            call.trace().success(Map.of("count", data.size(), "success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping("{id}/activation")
    public ResponseEntity<ApiResponse<ApplicationPermission>> setActivation(
            @PathVariable UUID id,
            @RequestParam boolean active,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        params.put("active", active);
        TracedCall call = tracedCall(log, "set-application-permission-activation", "PUT", BASE_PATH + "/{id}/activation", headers, null, params);
        try {
            ApplicationPermission updated = applicationPermissionService.setActive(id, active, call.context());
            call.trace().success(Map.of("applicationPermissionId", id, "active", active, "success", true));
            return ok(updated, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Object>> deleteById(
            @PathVariable UUID id,
            @RequestParam(name = "hard", defaultValue = "false") boolean hard,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "delete-application-permission", "DELETE", BASE_PATH + "/{id}", headers, null, Map.of("id", id));
        try {
            if (hard) {
            applicationPermissionService.hardDeleteById(id, call.context());
            } else {
            applicationPermissionService.deleteById(id, call.context());
            }
            call.trace().success(Map.of("deletedId", id, "success", true));
            return ok(null, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }
}
