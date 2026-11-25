package com.ayanlab.core.controller;

import com.ayanlab.core.condition.AuthPlatformApi;
import com.ayanlab.core.constant.ApiPathPrefixes;
import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.annotation.RequirePermission;
import com.ayanlab.core.dto.ClientHeadersPayload;
import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.core.entity.RolePolicyGroup;
import com.ayanlab.core.service.IRolePolicyGroupService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@AuthPlatformApi
@RestController
@RequestMapping(ApiPathPrefixes.AUTH + "/role-policy-groups")
@RequirePermission("core:roles:manage")
@Tag(name = "Auth · RolePolicyGroupController", description = "Role to policy group mappings")
@RequiredArgsConstructor
public class RolePolicyGroupController extends BaseController {

    private static final String BASE_PATH = ApiPathPrefixes.AUTH + "/role-policy-groups";
    private final IRolePolicyGroupService rolePolicyGroupService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<RolePolicyGroup>> create(
            @RequestBody RolePolicyGroup item,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "create-role-policy-group", "POST", BASE_PATH, headers, item, null);
        try {
            return ok(rolePolicyGroupService.create(item, call.context()), call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<RolePolicyGroup>> update(
            @PathVariable UUID id,
            @RequestBody RolePolicyGroup item,
            @ClientHeaders ClientHeadersPayload headers) {
        item.setId(id);
        TracedCall call = tracedCall(log, "update-role-policy-group", "PUT", BASE_PATH + "/{id}", headers, item, Map.of("id", id));
        try {
            return ok(rolePolicyGroupService.update(item, call.context()), call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @RequirePermission({"core:roles:read", "core:roles:manage"})

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<RolePolicyGroup>>> listByRole(
            @RequestParam UUID roleId,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "list-role-policy-groups", "GET", BASE_PATH, headers, null, Map.of("roleId", roleId));
        try {
            List<RolePolicyGroup> data = rolePolicyGroupService.findByRoleId(roleId, call.context());
            call.trace().success(Map.of("count", data.size(), "success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @RequirePermission({"core:roles:read", "core:roles:manage"})

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<RolePolicyGroup>> getById(
            @PathVariable UUID id,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "get-role-policy-group", "GET", BASE_PATH + "/{id}", headers, null, Map.of("id", id));
        try {
            return ok(rolePolicyGroupService.findById(id, call.context()), call.context());
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
        TracedCall call = tracedCall(log, "delete-role-policy-group", "DELETE", BASE_PATH + "/{id}", headers, null, Map.of("id", id));
        try {
            if (hard) {
            rolePolicyGroupService.hardDeleteById(id, call.context());
            } else {
            rolePolicyGroupService.deleteById(id, call.context());
            }
            return ok(null, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }
}
