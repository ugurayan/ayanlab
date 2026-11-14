package com.ayanlabs.core.controller;

import com.ayanlabs.core.annotation.ClientHeaders;
import com.ayanlabs.core.data.ClientHeadersPayload;
import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.core.entity.PolicyGroupPermission;
import com.ayanlabs.core.service.IPolicyGroupPermissionService;
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
@RestController
@RequestMapping("/api/core/policy-group-permissions")
@Tag(name = "Core · PolicyGroupPermissionController", description = "Policy group permission mappings")
@RequiredArgsConstructor
public class PolicyGroupPermissionController extends BaseController {

    private static final String BASE_PATH = "/api/core/policy-group-permissions";
    private final IPolicyGroupPermissionService policyGroupPermissionService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<PolicyGroupPermission>> create(
            @RequestBody PolicyGroupPermission item,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "create-policy-group-permission", "POST", BASE_PATH, headers, item, null);
        try {
            PolicyGroupPermission created = policyGroupPermissionService.create(item, call.context());
            return ok(created, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<PolicyGroupPermission>> update(
            @PathVariable UUID id,
            @RequestBody PolicyGroupPermission item,
            @ClientHeaders ClientHeadersPayload headers) {
        item.setId(id);
        TracedCall call = tracedCall(log, "update-policy-group-permission", "PUT", BASE_PATH + "/{id}", headers, item, Map.of("id", id));
        try {
            return ok(policyGroupPermissionService.update(item, call.context()), call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<PolicyGroupPermission>>> listByPolicyGroup(
            @RequestParam UUID policyGroupId,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "list-policy-group-permissions", "GET", BASE_PATH, headers, null, Map.of("policyGroupId", policyGroupId));
        try {
            List<PolicyGroupPermission> data = policyGroupPermissionService.findByPolicyGroupId(policyGroupId, call.context());
            call.trace().success(Map.of("count", data.size(), "success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<PolicyGroupPermission>> getById(
            @PathVariable UUID id,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "get-policy-group-permission", "GET", BASE_PATH + "/{id}", headers, null, Map.of("id", id));
        try {
            return ok(policyGroupPermissionService.findById(id, call.context()), call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Object>> deleteById(
            @PathVariable UUID id,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "delete-policy-group-permission", "DELETE", BASE_PATH + "/{id}", headers, null, Map.of("id", id));
        try {
            policyGroupPermissionService.deleteById(id, call.context());
            return ok(null, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }
}
