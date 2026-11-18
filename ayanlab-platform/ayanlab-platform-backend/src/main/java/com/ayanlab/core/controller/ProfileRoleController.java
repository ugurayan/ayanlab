package com.ayanlab.core.controller;

import com.ayanlab.core.condition.AuthPlatformApi;
import com.ayanlab.core.web.ApiPathPrefixes;
import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.annotation.RequirePermission;
import com.ayanlab.core.data.ClientHeadersPayload;
import com.ayanlab.core.data.responses.ApiResponse;
import com.ayanlab.core.entity.ProfileRole;
import com.ayanlab.core.service.IProfileRoleService;
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
@RequestMapping(ApiPathPrefixes.AUTH + "/profile-roles")
@RequirePermission("core:profiles:manage")
@Tag(name = "Auth · ProfileRoleController", description = "Profile to role assignments")
@RequiredArgsConstructor
public class ProfileRoleController extends BaseController {

    private static final String BASE_PATH = ApiPathPrefixes.AUTH + "/profile-roles";
    private final IProfileRoleService profileRoleService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ProfileRole>> create(
            @RequestBody ProfileRole item,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "create-profile-role", "POST", BASE_PATH, headers, item, null);
        try {
            ProfileRole created = profileRoleService.create(item, call.context());
            call.trace().success(Map.of("createdId", created.getId(), "success", true));
            return ok(created, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ProfileRole>> update(
            @PathVariable UUID id,
            @RequestBody ProfileRole item,
            @ClientHeaders ClientHeadersPayload headers) {
        item.setId(id);
        TracedCall call = tracedCall(log, "update-profile-role", "PUT", BASE_PATH + "/{id}", headers, item, Map.of("id", id));
        try {
            return ok(profileRoleService.update(item, call.context()), call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @RequirePermission({"core:profiles:read", "core:profiles:manage"})

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ProfileRole>> getById(
            @PathVariable UUID id,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "get-profile-role", "GET", BASE_PATH + "/{id}", headers, null, Map.of("id", id));
        try {
            return ok(profileRoleService.findById(id, call.context()), call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @RequirePermission({"core:profiles:read", "core:profiles:manage"})

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<ProfileRole>>> listByProfile(
            @RequestParam UUID profileId,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "list-profile-roles", "GET", BASE_PATH, headers, null, Map.of("profileId", profileId));
        try {
            List<ProfileRole> data = profileRoleService.findByProfileId(profileId, call.context());
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
        TracedCall call = tracedCall(log, "delete-profile-role", "DELETE", BASE_PATH + "/{id}", headers, null, Map.of("id", id));
        try {
            if (hard) {
            profileRoleService.hardDeleteById(id, call.context());
            } else {
            profileRoleService.deleteById(id, call.context());
            }
            call.trace().success(Map.of("deletedId", id, "success", true));
            return ok(null, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }
}
