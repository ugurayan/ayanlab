package com.ayanlab.core.controller;

import com.ayanlab.core.condition.AuthPlatformApi;
import com.ayanlab.core.constant.ApiPathPrefixes;
import com.ayanlab.core.dto.ClientHeadersPayload;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.annotation.RequirePermission;

import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.core.entity.Role;
import com.ayanlab.core.util.ApiCallLogger;
import com.ayanlab.core.service.IRoleService;
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
@RequestMapping(ApiPathPrefixes.AUTH + "/roles")
@RequirePermission("core:roles:manage")
@Tag(name = "Auth · RoleController", description = "Roles")
@RequiredArgsConstructor
public class RoleController extends BaseController {

    private static final String BASE_PATH = ApiPathPrefixes.AUTH + "/roles";

    private final IRoleService roleService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Role>> create(
            @RequestBody Role role,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        TracedCall call = tracedCall(log, "create-role", "POST", BASE_PATH, headers, role, null);
        try {
            Role created = roleService.create(role, call.context());
            ResponseEntity<ApiResponse<Role>> response = ok(created, call.context());
            call.trace().success(Map.of("createdId", created != null ? created.getId() : null, "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Role>> update(
            @PathVariable UUID id,
            @RequestBody Role role,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        TracedCall call = tracedCall(log, "update-role", "PUT", BASE_PATH + "/{id}", headers, role, params);
        try {
            role.setId(id);
            Role updated = roleService.update(role, call.context());
            ResponseEntity<ApiResponse<Role>> response = ok(updated, call.context());
            call.trace().success(Map.of("updatedId", updated != null ? updated.getId() : null, "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @RequirePermission({"core:roles:read", "core:roles:manage"})

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<Role>>> list(
            @RequestParam(required = false) UUID appId,
            @RequestParam(required = false, defaultValue = "false") boolean includeInactive,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("appId", appId);
        params.put("includeInactive", includeInactive);
        TracedCall call = tracedCall(log, "list-roles", "GET", BASE_PATH, headers, null, params);
        try {
            List<Role> data = roleService.findAll(appId, includeInactive, call.context());
            ResponseEntity<ApiResponse<List<Role>>> response = ok(data, call.context());
            call.trace().success(Map.of("count", data.size(), "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @RequirePermission({"core:roles:read", "core:roles:manage"})

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Role>> getById(
            @PathVariable UUID id,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        TracedCall call = tracedCall(log, "get-role", "GET", BASE_PATH + "/{id}", headers, null, params);
        try {
            Role found = roleService.findById(id, call.context());
            ResponseEntity<ApiResponse<Role>> response = ok(found, call.context());
            call.trace().success(Map.of("foundId", found != null ? found.getId() : null, "success", found != null));
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
        TracedCall call = tracedCall(log, "delete-role", "DELETE", BASE_PATH + "/{id}", headers, null, params);
        try {
            if (hard) {
            roleService.hardDeleteById(id, call.context());
            } else {
            roleService.deleteById(id, call.context());
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
    public ResponseEntity<ApiResponse<Role>> setActivation(
            @PathVariable UUID id,
            @RequestParam boolean active,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        params.put("active", active);
        TracedCall call = tracedCall(log, "set-activation", "PUT", BASE_PATH + "/{id}/activation", headers, null, params);
        try {
            Role updated = roleService.setActive(id, active, call.context());
            ResponseEntity<ApiResponse<Role>> response = ok(updated, call.context());
            call.trace().success(Map.of("id", id, "active", active, "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

}
