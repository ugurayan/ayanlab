package com.ayanlab.hrm.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.annotation.RequirePermission;
import com.ayanlab.core.controller.BaseController;
import com.ayanlab.core.data.ClientHeadersPayload;
import com.ayanlab.core.data.responses.ApiResponse;
import com.ayanlab.hrm.backend.entity.ProfileLocationDetail;
import com.ayanlab.hrm.backend.service.ProfileLocationDetailService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/hrm/api/profile-location-details")
@RequiredArgsConstructor
@Tag(name = "HRM · ProfileLocationDetail", description = "ProfileLocationDetail CRUD")
public class ProfileLocationDetailController extends BaseController {

    private static final String BASE = "/hrm/api/profile-location-details";
    private final ProfileLocationDetailService service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:profile_locations:read")
    public ResponseEntity<ApiResponse<List<ProfileLocationDetail>>> list(@ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "profile_location_details-list", "GET", BASE, headers, null, null);
        try {
            List<ProfileLocationDetail> data = service.findAll(call.context());
            call.trace().success(Map.of("count", data.size()));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:profile_locations:read")
    public ResponseEntity<ApiResponse<ProfileLocationDetail>> get(
            @PathVariable String id, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "profile_location_details-get", "GET", BASE + "/{id}", headers, null, Map.of("id", id));
        try {
            ProfileLocationDetail data = service.findById(id, call.context());
            call.trace().success(Map.of("id", id));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:profile_locations:create")
    public ResponseEntity<ApiResponse<ProfileLocationDetail>> create(
            @RequestBody ProfileLocationDetail body, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "profile_location_details-create", "POST", BASE, headers, body, null);
        try {
            ProfileLocationDetail created = service.create(body, call.context());
            call.trace().success(Map.of("id", created.getId()));
            return createdBody(created, "ProfileLocationDetail created", call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:profile_locations:update")
    public ResponseEntity<ApiResponse<ProfileLocationDetail>> update(
            @PathVariable String id, @RequestBody ProfileLocationDetail body, @ClientHeaders ClientHeadersPayload headers) {
        body.setId(id);
        TracedCall call = tracedCall(log, "profile_location_details-update", "PUT", BASE + "/{id}", headers, body, Map.of("id", id));
        try {
            ProfileLocationDetail updated = service.update(body, call.context());
            call.trace().success(Map.of("id", id));
            return ok(updated, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @RequirePermission("staffiq:profile_locations:delete")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable String id, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "profile_location_details-delete", "DELETE", BASE + "/{id}", headers, null, Map.of("id", id));
        try {
            service.deleteById(id, call.context());
            call.trace().success(Map.of("id", id));
            return ok(null, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }
}
