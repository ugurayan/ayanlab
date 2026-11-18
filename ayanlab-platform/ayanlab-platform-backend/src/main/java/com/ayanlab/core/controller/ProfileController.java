package com.ayanlab.core.controller;

import com.ayanlab.core.condition.AuthPlatformApi;
import com.ayanlab.core.web.ApiPathPrefixes;
import com.ayanlab.core.data.ClientHeadersPayload;

import com.ayanlab.core.annotation.ClientHeaders;

import com.ayanlab.core.data.responses.ApiResponse;
import com.ayanlab.core.entity.UserProfile;
import com.ayanlab.core.service.IProfileService;
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
@RequestMapping(ApiPathPrefixes.AUTH + "/profiles")
@Tag(name = "Auth · ProfileController", description = "Profiles")
@RequiredArgsConstructor
public class ProfileController extends BaseController {

    private static final String BASE_PATH = ApiPathPrefixes.AUTH + "/profiles";

    private final IProfileService profileService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<UserProfile>> create(
            @RequestBody UserProfile profile,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        TracedCall call = tracedCall(log, "create-profile", "POST", BASE_PATH, headers, profile, null);
        try {
            UserProfile created = profileService.create(profile, call.context());
            ResponseEntity<ApiResponse<UserProfile>> response = ok(created, call.context());
            call.trace().success(Map.of("createdId", created != null ? created.getId() : null, "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<UserProfile>> update(
            @PathVariable UUID id,
            @RequestBody UserProfile profile,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        TracedCall call = tracedCall(log, "update-profile", "PUT", BASE_PATH + "/{id}", headers, profile, params);
        try {
            profile.setId(id);
            UserProfile updated = profileService.update(profile, call.context());
            ResponseEntity<ApiResponse<UserProfile>> response = ok(updated, call.context());
            call.trace().success(Map.of("updatedId", updated != null ? updated.getId() : null, "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<UserProfile>>> list(
            @RequestParam(required = false) UUID appId,
            @RequestParam(required = false) UUID userId,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("appId", appId);
        params.put("userId", userId);
        TracedCall call = tracedCall(log, "list-profiles", "GET", BASE_PATH, headers, null, params);
        try {
            List<UserProfile> data = userId != null
                ? profileService.findByUser(userId, call.context())
                : profileService.findByApp(appId, call.context());
            ResponseEntity<ApiResponse<List<UserProfile>>> response = ok(data, call.context());
            call.trace().success(Map.of("count", data.size(), "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<UserProfile>> getById(
            @PathVariable UUID id,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        TracedCall call = tracedCall(log, "get-profile", "GET", BASE_PATH + "/{id}", headers, null, params);
        try {
            UserProfile found = profileService.findById(id, call.context());
            ResponseEntity<ApiResponse<UserProfile>> response = ok(found, call.context());
            call.trace().success(Map.of("foundId", found != null ? found.getId() : null, "success", found != null));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<ApiResponse<Object>> deleteById(
            @PathVariable UUID id,
            @RequestParam(name = "hard", defaultValue = "false") boolean hard,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        TracedCall call = tracedCall(log, "delete-profile", "DELETE", BASE_PATH + "/{id}", headers, null, params);
        try {
            if (hard) {
            profileService.hardDeleteById(id, call.context());
            } else {
            profileService.deleteById(id, call.context());
            }
            ResponseEntity<ApiResponse<Object>> response = ok(null, call.context());
            call.trace().success(Map.of("deletedId", id, "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "{id}/activation")
    public ResponseEntity<ApiResponse<UserProfile>> setActivation(
            @PathVariable UUID id,
            @RequestParam boolean active,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        params.put("active", active);
        TracedCall call = tracedCall(log, "set-activation", "PUT", BASE_PATH + "/{id}/activation", headers, null, params);
        try {
            UserProfile updated = profileService.setActive(id, active, call.context());
            ResponseEntity<ApiResponse<UserProfile>> response = ok(updated, call.context());
            call.trace().success(Map.of("id", id, "active", active, "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

}
