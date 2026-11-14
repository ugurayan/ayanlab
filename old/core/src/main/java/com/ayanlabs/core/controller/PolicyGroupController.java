package com.ayanlabs.core.controller;

import com.ayanlabs.core.annotation.ClientHeaders;
import com.ayanlabs.core.data.ClientHeadersPayload;
import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.core.entity.PolicyGroup;
import com.ayanlabs.core.service.IPolicyGroupService;
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
@RequestMapping("/api/core/policy-groups")
@Tag(name = "Core · PolicyGroupController", description = "App-scoped policy groups")
@RequiredArgsConstructor
public class PolicyGroupController extends BaseController {

    private static final String BASE_PATH = "/api/core/policy-groups";
    private final IPolicyGroupService policyGroupService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<PolicyGroup>> create(
            @RequestBody PolicyGroup policyGroup,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "create-policy-group", "POST", BASE_PATH, headers, policyGroup, null);
        try {
            PolicyGroup created = policyGroupService.create(policyGroup, call.context());
            ResponseEntity<ApiResponse<PolicyGroup>> response = ok(created, call.context());
            call.trace().success(Map.of("createdId", created.getId(), "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<PolicyGroup>> update(
            @PathVariable UUID id,
            @RequestBody PolicyGroup policyGroup,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        TracedCall call = tracedCall(log, "update-policy-group", "PUT", BASE_PATH + "/{id}", headers, policyGroup, params);
        try {
            policyGroup.setId(id);
            PolicyGroup updated = policyGroupService.update(policyGroup, call.context());
            ResponseEntity<ApiResponse<PolicyGroup>> response = ok(updated, call.context());
            call.trace().success(Map.of("updatedId", updated.getId(), "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<PolicyGroup>> getById(
            @PathVariable UUID id,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        TracedCall call = tracedCall(log, "get-policy-group", "GET", BASE_PATH + "/{id}", headers, null, params);
        try {
            PolicyGroup found = policyGroupService.findById(id, call.context());
            ResponseEntity<ApiResponse<PolicyGroup>> response = ok(found, call.context());
            call.trace().success(Map.of("foundId", found != null ? found.getId() : null, "success", found != null));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<PolicyGroup>>> listByApp(
            @RequestParam(required = false) UUID appId,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "list-policy-groups", "GET", BASE_PATH, headers, null, Map.of("appId", appId));
        try {
            List<PolicyGroup> data = policyGroupService.findByApp(appId, call.context());
            ResponseEntity<ApiResponse<List<PolicyGroup>>> response = ok(data, call.context());
            call.trace().success(Map.of("count", data.size(), "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Object>> deleteById(
            @PathVariable UUID id,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        TracedCall call = tracedCall(log, "delete-policy-group", "DELETE", BASE_PATH + "/{id}", headers, null, params);
        try {
            policyGroupService.deleteById(id, call.context());
            ResponseEntity<ApiResponse<Object>> response = ok(null, call.context());
            call.trace().success(Map.of("deletedId", id, "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }
}
