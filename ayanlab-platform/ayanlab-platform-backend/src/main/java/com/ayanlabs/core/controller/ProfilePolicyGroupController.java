package com.ayanlabs.core.controller;

import com.ayanlabs.core.condition.AuthPlatformApi;
import com.ayanlabs.core.web.ApiPathPrefixes;
import com.ayanlabs.core.annotation.ClientHeaders;
import com.ayanlabs.core.data.ClientHeadersPayload;
import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.core.entity.ProfilePolicyGroup;
import com.ayanlabs.core.service.IProfilePolicyGroupService;
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
@RequestMapping(ApiPathPrefixes.AUTH + "/profile-policy-groups")
@Tag(name = "Auth · ProfilePolicyGroupController", description = "Profile to policy group assignments")
@RequiredArgsConstructor
public class ProfilePolicyGroupController extends BaseController {

    private static final String BASE_PATH = ApiPathPrefixes.AUTH + "/profile-policy-groups";
    private final IProfilePolicyGroupService profilePolicyGroupService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ProfilePolicyGroup>> create(
            @RequestBody ProfilePolicyGroup item,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "create-profile-policy-group", "POST", BASE_PATH, headers, item, null);
        try {
            return ok(profilePolicyGroupService.create(item, call.context()), call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ProfilePolicyGroup>> update(
            @PathVariable UUID id,
            @RequestBody ProfilePolicyGroup item,
            @ClientHeaders ClientHeadersPayload headers) {
        item.setId(id);
        TracedCall call = tracedCall(log, "update-profile-policy-group", "PUT", BASE_PATH + "/{id}", headers, item, Map.of("id", id));
        try {
            return ok(profilePolicyGroupService.update(item, call.context()), call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<ProfilePolicyGroup>>> listByProfile(
            @RequestParam UUID profileId,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "list-profile-policy-groups", "GET", BASE_PATH, headers, null, Map.of("profileId", profileId));
        try {
            List<ProfilePolicyGroup> data = profilePolicyGroupService.findByProfileId(profileId, call.context());
            call.trace().success(Map.of("count", data.size(), "success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ProfilePolicyGroup>> getById(
            @PathVariable UUID id,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "get-profile-policy-group", "GET", BASE_PATH + "/{id}", headers, null, Map.of("id", id));
        try {
            return ok(profilePolicyGroupService.findById(id, call.context()), call.context());
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
        TracedCall call = tracedCall(log, "delete-profile-policy-group", "DELETE", BASE_PATH + "/{id}", headers, null, Map.of("id", id));
        try {
            if (hard) {
            profilePolicyGroupService.hardDeleteById(id, call.context());
            } else {
            profilePolicyGroupService.deleteById(id, call.context());
            }
            return ok(null, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }
}
