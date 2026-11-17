package com.ayanlabs.core.controller;

import com.ayanlabs.core.condition.AuthPlatformApi;
import com.ayanlabs.core.web.ApiPathPrefixes;
import com.ayanlabs.core.data.ClientHeadersPayload;

import com.ayanlabs.core.annotation.ClientHeaders;

import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.core.entity.RolePermission;
import com.ayanlabs.core.service.IRolePermissionService;
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
@RequestMapping(ApiPathPrefixes.AUTH + "/role-permissions")
@Tag(name = "Auth · RolePermissionController", description = "Role–permission mappings")
@RequiredArgsConstructor
public class RolePermissionController extends BaseController {

    private static final String BASE_PATH = ApiPathPrefixes.AUTH + "/role-permissions";

    private final IRolePermissionService rolePermissionService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<RolePermission>> create(
            @RequestBody RolePermission rolePermission,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        TracedCall call = tracedCall(log, "create-rolePermission", "POST", BASE_PATH, headers, rolePermission, null);
        try {
            RolePermission created = rolePermissionService.create(rolePermission, call.context());
            ResponseEntity<ApiResponse<RolePermission>> response = ok(created, call.context());
            call.trace().success(Map.of("createdId", created.getId(), "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<RolePermission>> update(
            @PathVariable UUID id,
            @RequestBody RolePermission rolePermission,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        TracedCall call = tracedCall(log, "update-rolePermission", "PUT", BASE_PATH + "/{id}", headers, rolePermission, params);
        try {
            rolePermission.setId(id);
            RolePermission updated = rolePermissionService.update(rolePermission, call.context());
            ResponseEntity<ApiResponse<RolePermission>> response = ok(updated, call.context());
            call.trace().success(Map.of("updatedId", updated.getId(), "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<RolePermission>>> listByRole(
            @RequestParam UUID roleId,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        TracedCall call = tracedCall(log, "list-role-permissions", "GET", BASE_PATH, headers, null, Map.of("roleId", roleId));
        try {
            List<RolePermission> data = rolePermissionService.findByRoleId(roleId, call.context());
            ResponseEntity<ApiResponse<List<RolePermission>>> response = ok(data, call.context());
            call.trace().success(Map.of("count", data.size(), "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<RolePermission>> getById(
            @PathVariable UUID id,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        TracedCall call = tracedCall(log, "get-rolePermission", "GET", BASE_PATH + "/{id}", headers, null, params);
        try {
            RolePermission found = rolePermissionService.findById(id, call.context());
            ResponseEntity<ApiResponse<RolePermission>> response = ok(found, call.context());
            call.trace().success(Map.of(
                    "foundId", found != null ? found.getId() : null,
                    "success", found != null));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<ApiResponse<Object>> deleteById(
            @PathVariable UUID id,
            @RequestParam(name = "hard", defaultValue = "false") boolean hard,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        TracedCall call = tracedCall(log, "delete-rolePermission", "DELETE", BASE_PATH + "/{id}", headers, null, params);
        try {
            if (hard) {
            rolePermissionService.hardDeleteById(id, call.context());
            } else {
            rolePermissionService.deleteById(id, call.context());
            }
            ResponseEntity<ApiResponse<Object>> response = ok(null, call.context());
            call.trace().success(Map.of("deletedId", id, "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "{id}/activation")
    public ResponseEntity<ApiResponse<RolePermission>> setActivation(
            @PathVariable UUID id,
            @RequestParam boolean active,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        params.put("active", active);
        TracedCall call = tracedCall(log, "set-activation", "PUT", BASE_PATH + "/{id}/activation", headers, null, params);
        try {
            RolePermission updated = rolePermissionService.setActive(id, active, call.context());
            ResponseEntity<ApiResponse<RolePermission>> response = ok(updated, call.context());
            call.trace().success(Map.of("id", id, "active", active, "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

}
