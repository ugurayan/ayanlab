package com.ayanlabs.core.controller;

import com.ayanlabs.core.condition.AuthPlatformApi;
import com.ayanlabs.core.web.ApiPathPrefixes;
import com.ayanlabs.core.annotation.ClientHeaders;
import com.ayanlabs.core.data.ClientHeadersPayload;
import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.core.entity.RolePolicyGroup;
import com.ayanlabs.core.service.IRolePolicyGroupService;
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
