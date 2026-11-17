package com.ayanlabs.core.controller;

import com.ayanlabs.core.annotation.ClientHeaders;
import com.ayanlabs.core.condition.AuthPlatformApi;
import com.ayanlabs.core.data.ClientHeadersPayload;
import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.core.entity.ApplicationOrganization;
import com.ayanlabs.core.service.IApplicationOrganizationService;
import com.ayanlabs.core.web.ApiPathPrefixes;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@AuthPlatformApi
@RestController
@RequestMapping(ApiPathPrefixes.AUTH + "/application-organizations")
@Tag(name = "Auth · ApplicationOrganizationController", description = "App to organization mappings")
@RequiredArgsConstructor
public class ApplicationOrganizationController extends BaseController {

    private static final String BASE_PATH = ApiPathPrefixes.AUTH + "/application-organizations";
    private final IApplicationOrganizationService applicationOrganizationService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ApplicationOrganization>> create(
            @RequestBody ApplicationOrganization item,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "create-application-organization", "POST", BASE_PATH, headers, item, null);
        try {
            ApplicationOrganization created = applicationOrganizationService.create(item, call.context());
            call.trace().success(Map.of("createdId", created.getId(), "success", true));
            return ok(created, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ApplicationOrganization>> update(
            @PathVariable UUID id,
            @RequestBody ApplicationOrganization item,
            @ClientHeaders ClientHeadersPayload headers) {
        item.setId(id);
        TracedCall call = tracedCall(log, "update-application-organization", "PUT", BASE_PATH + "/{id}", headers, item, Map.of("id", id));
        try {
            return ok(applicationOrganizationService.update(item, call.context()), call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ApplicationOrganization>> getById(
            @PathVariable UUID id,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "get-application-organization", "GET", BASE_PATH + "/{id}", headers, null, Map.of("id", id));
        try {
            return ok(applicationOrganizationService.findById(id, call.context()), call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<ApplicationOrganization>>> list(
            @RequestParam(required = false) UUID appId,
            @RequestParam(required = false) Boolean includeInactive,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("appId", appId);
        params.put("includeInactive", includeInactive);
        TracedCall call = tracedCall(log, "list-application-organizations", "GET", BASE_PATH, headers, null, params);
        try {
            List<ApplicationOrganization> data =
                    applicationOrganizationService.list(appId, Boolean.TRUE.equals(includeInactive), call.context());
            call.trace().success(Map.of("count", data.size(), "success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping("{id}/activation")
    public ResponseEntity<ApiResponse<ApplicationOrganization>> setActivation(
            @PathVariable UUID id,
            @RequestParam boolean active,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        params.put("active", active);
        TracedCall call = tracedCall(log, "set-application-organization-activation", "PUT", BASE_PATH + "/{id}/activation", headers, null, params);
        try {
            ApplicationOrganization updated = applicationOrganizationService.setActive(id, active, call.context());
            call.trace().success(Map.of("applicationOrganizationId", id, "active", active, "success", true));
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
        TracedCall call = tracedCall(log, "delete-application-organization", "DELETE", BASE_PATH + "/{id}", headers, null, Map.of("id", id));
        try {
            if (hard) {
                applicationOrganizationService.hardDeleteById(id, call.context());
            } else {
                applicationOrganizationService.deleteById(id, call.context());
            }
            call.trace().success(Map.of("deletedId", id, "success", true));
            return ok(null, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }
}
