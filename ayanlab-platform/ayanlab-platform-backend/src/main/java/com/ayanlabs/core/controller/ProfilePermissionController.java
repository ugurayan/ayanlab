package com.ayanlabs.core.controller;

import com.ayanlabs.core.condition.AuthPlatformApi;
import com.ayanlabs.core.web.ApiPathPrefixes;
import com.ayanlabs.core.annotation.ClientHeaders;
import com.ayanlabs.core.data.ClientHeadersPayload;
import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.core.entity.ProfilePermission;
import com.ayanlabs.core.service.IProfilePermissionService;
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
@RequestMapping(ApiPathPrefixes.AUTH + "/profile-permissions")
@Tag(name = "Auth · ProfilePermissionController", description = "Profile-level permission grants and denials")
@RequiredArgsConstructor
public class ProfilePermissionController extends BaseController {

    private static final String BASE_PATH = ApiPathPrefixes.AUTH + "/profile-permissions";
    private final IProfilePermissionService profilePermissionService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ProfilePermission>> create(
            @RequestBody ProfilePermission item,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "create-profile-permission", "POST", BASE_PATH, headers, item, null);
        try {
            ProfilePermission created = profilePermissionService.create(item, call.context());
            call.trace().success(Map.of("createdId", created.getId(), "success", true));
            return ok(created, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ProfilePermission>> update(
            @PathVariable UUID id,
            @RequestBody ProfilePermission item,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        item.setId(id);
        TracedCall call = tracedCall(log, "update-profile-permission", "PUT", BASE_PATH + "/{id}", headers, item, params);
        try {
            ProfilePermission updated = profilePermissionService.update(item, call.context());
            call.trace().success(Map.of("updatedId", updated.getId(), "success", true));
            return ok(updated, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ProfilePermission>> getById(
            @PathVariable UUID id,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        TracedCall call = tracedCall(log, "get-profile-permission", "GET", BASE_PATH + "/{id}", headers, null, params);
        try {
            ProfilePermission found = profilePermissionService.findById(id, call.context());
            call.trace().success(Map.of("foundId", found != null ? found.getId() : null, "success", found != null));
            return ok(found, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<ProfilePermission>>> listByProfile(
            @RequestParam UUID profileId,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "list-profile-permissions", "GET", BASE_PATH, headers, null, Map.of("profileId", profileId));
        try {
            List<ProfilePermission> data = profilePermissionService.findByProfileId(profileId, call.context());
            call.trace().success(Map.of("count", data.size(), "success", true));
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
        TracedCall call = tracedCall(log, "delete-profile-permission", "DELETE", BASE_PATH + "/{id}", headers, null, Map.of("id", id));
        try {
            if (hard) {
            profilePermissionService.hardDeleteById(id, call.context());
            } else {
            profilePermissionService.deleteById(id, call.context());
            }
            call.trace().success(Map.of("deletedId", id, "success", true));
            return ok(null, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }
}
