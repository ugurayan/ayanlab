package com.ayanlab.core.controller;

import com.ayanlab.core.condition.CoreBusinessApi;
import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.annotation.RequirePermission;
import com.ayanlab.core.data.ClientHeadersPayload;
import com.ayanlab.core.data.responses.ApiResponse;
import com.ayanlab.core.entity.ProfileResourceScope;
import com.ayanlab.core.service.IProfileResourceScopeService;
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
@CoreBusinessApi
@RestController
@RequestMapping("/core/api/profile-resource-scopes")
@RequirePermission("core:profiles:manage")
@Tag(name = "Core · ProfileResourceScopeController", description = "Profile row-level resource scopes")
@RequiredArgsConstructor
public class ProfileResourceScopeController extends BaseController {

    private static final String BASE_PATH = "/core/api/profile-resource-scopes";
    private final IProfileResourceScopeService profileResourceScopeService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ProfileResourceScope>> create(
            @RequestBody ProfileResourceScope item,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "create-profile-resource-scope", "POST", BASE_PATH, headers, item, null);
        try {
            return ok(profileResourceScopeService.create(item, call.context()), call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ProfileResourceScope>> update(
            @PathVariable UUID id,
            @RequestBody ProfileResourceScope item,
            @ClientHeaders ClientHeadersPayload headers) {
        item.setId(id);
        TracedCall call = tracedCall(log, "update-profile-resource-scope", "PUT", BASE_PATH + "/{id}", headers, item, Map.of("id", id));
        try {
            return ok(profileResourceScopeService.update(item, call.context()), call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @RequirePermission({"core:profiles:read", "core:profiles:manage"})

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ProfileResourceScope>> getById(
            @PathVariable UUID id,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "get-profile-resource-scope", "GET", BASE_PATH + "/{id}", headers, null, Map.of("id", id));
        try {
            return ok(profileResourceScopeService.findById(id, call.context()), call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @RequirePermission({"core:profiles:read", "core:profiles:manage"})

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<ProfileResourceScope>>> listByProfile(
            @RequestParam UUID profileId,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "list-profile-resource-scopes", "GET", BASE_PATH, headers, null, Map.of("profileId", profileId));
        try {
            List<ProfileResourceScope> data = profileResourceScopeService.findByProfile(profileId, call.context());
            return ok(data, call.context());
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
        TracedCall call = tracedCall(log, "delete-profile-resource-scope", "DELETE", BASE_PATH + "/{id}", headers, null, Map.of("id", id));
        try {
            if (hard) {
            profileResourceScopeService.hardDeleteById(id, call.context());
            } else {
            profileResourceScopeService.deleteById(id, call.context());
            }
            return ok(null, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }
}
