package com.ayanlabs.core.controller;

import com.ayanlabs.core.condition.AuthPlatformApi;
import com.ayanlabs.core.web.ApiPathPrefixes;
import com.ayanlabs.core.annotation.ClientHeaders;
import com.ayanlabs.core.data.ClientHeadersPayload;
import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.core.entity.ApplicationRole;
import com.ayanlabs.core.service.IApplicationRoleService;
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
@RequestMapping(ApiPathPrefixes.AUTH + "/application-roles")
@Tag(name = "Auth · ApplicationRoleController", description = "App to role mappings")
@RequiredArgsConstructor
public class ApplicationRoleController extends BaseController {

    private static final String BASE_PATH = ApiPathPrefixes.AUTH + "/application-roles";
    private final IApplicationRoleService applicationRoleService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ApplicationRole>> create(
            @RequestBody ApplicationRole item,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "create-application-role", "POST", BASE_PATH, headers, item, null);
        try {
            ApplicationRole created = applicationRoleService.create(item, call.context());
            call.trace().success(Map.of("createdId", created.getId(), "success", true));
            return ok(created, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ApplicationRole>> update(
            @PathVariable UUID id,
            @RequestBody ApplicationRole item,
            @ClientHeaders ClientHeadersPayload headers) {
        item.setId(id);
        TracedCall call = tracedCall(log, "update-application-role", "PUT", BASE_PATH + "/{id}", headers, item, Map.of("id", id));
        try {
            return ok(applicationRoleService.update(item, call.context()), call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ApplicationRole>> getById(
            @PathVariable UUID id,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "get-application-role", "GET", BASE_PATH + "/{id}", headers, null, Map.of("id", id));
        try {
            return ok(applicationRoleService.findById(id, call.context()), call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<ApplicationRole>>> list(
            @RequestParam(required = false) UUID appId,
            @RequestParam(required = false) Boolean includeInactive,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("appId", appId);
        params.put("includeInactive", includeInactive);
        TracedCall call = tracedCall(log, "list-application-roles", "GET", BASE_PATH, headers, null, params);
        try {
            List<ApplicationRole> data =
                    applicationRoleService.list(appId, Boolean.TRUE.equals(includeInactive), call.context());
            call.trace().success(Map.of("count", data.size(), "success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping("{id}/activation")
    public ResponseEntity<ApiResponse<ApplicationRole>> setActivation(
            @PathVariable UUID id,
            @RequestParam boolean active,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        params.put("active", active);
        TracedCall call = tracedCall(log, "set-application-role-activation", "PUT", BASE_PATH + "/{id}/activation", headers, null, params);
        try {
            ApplicationRole updated = applicationRoleService.setActive(id, active, call.context());
            call.trace().success(Map.of("applicationRoleId", id, "active", active, "success", true));
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
        TracedCall call = tracedCall(log, "delete-application-role", "DELETE", BASE_PATH + "/{id}", headers, null, Map.of("id", id));
        try {
            if (hard) {
            applicationRoleService.hardDeleteById(id, call.context());
            } else {
            applicationRoleService.deleteById(id, call.context());
            }
            call.trace().success(Map.of("deletedId", id, "success", true));
            return ok(null, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }
}
