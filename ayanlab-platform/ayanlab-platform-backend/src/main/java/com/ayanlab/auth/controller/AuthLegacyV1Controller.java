package com.ayanlab.auth.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.controller.BaseController;
import com.ayanlab.core.data.ClientHeadersPayload;
import com.ayanlab.core.data.responses.ApiResponse;
import com.ayanlab.core.entity.Role;
import com.ayanlab.core.service.IRoleService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * Compatibility shim for legacy {@code tmp/auth} paths ({@code /api/v1/role/*}).
 */
@Slf4j
@Hidden
@RestController
@RequestMapping("/auth/api/v1/role")
@RequiredArgsConstructor
public class AuthLegacyV1Controller extends BaseController {

    private final IRoleService roleService;

    @PostMapping(value = "create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Role>> create(
            @Valid @RequestBody Role role,
            @ClientHeaders ClientHeadersPayload headers,
            @RequestParam(defaultValue = "1000") String correlationId) {
        TracedCall call = tracedCall(log, "legacy-create-role", "POST", "/auth/api/v1/role/create", headers, role,
            Map.of("correlationId", correlationId));
        Role created = roleService.create(role, call.context());
        return ok(created, call.context());
    }

    @PutMapping(value = "update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Role>> update(
            @PathVariable UUID id,
            @Valid @RequestBody Role role,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "legacy-update-role", "PUT", "/auth/api/v1/role/update/{id}", headers, role, Map.of("id", id));
        role.setId(id);
        return ok(roleService.update(role, call.context()), call.context());
    }

    @GetMapping(value = "get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Role>> get(@PathVariable UUID id, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "legacy-get-role", "GET", "/auth/api/v1/role/get/{id}", headers, null, Map.of("id", id));
        return ok(roleService.findById(id, call.context()), call.context());
    }

    @DeleteMapping(value = "delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable UUID id, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "legacy-delete-role", "DELETE", "/auth/api/v1/role/delete/{id}", headers, null, Map.of("id", id));
        roleService.deleteById(id, call.context());
        return ok(null, call.context());
    }
}