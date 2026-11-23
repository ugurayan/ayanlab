package com.ayanlab.hrm.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.annotation.RequirePermission;
import com.ayanlab.core.controller.BaseController;
import com.ayanlab.core.data.ClientHeadersPayload;
import com.ayanlab.core.data.responses.ApiResponse;
import com.ayanlab.hrm.backend.entity.ProfileContact;
import com.ayanlab.hrm.backend.service.ProfileContactService;
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
@RequestMapping("/hrm/api/profile-contacts")
@RequiredArgsConstructor
@Tag(name = "HRM · ProfileContact", description = "ProfileContact CRUD")
public class ProfileContactController extends BaseController {

    private static final String BASE = "/hrm/api/profile-contacts";
    private final ProfileContactService service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:contacts:read")
    public ResponseEntity<ApiResponse<List<ProfileContact>>> list(@ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "profile_contacts-list", "GET", BASE, headers, null, null);
        try {
            List<ProfileContact> data = service.findAll(call.context());
            call.trace().success(Map.of("count", data.size()));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:contacts:read")
    public ResponseEntity<ApiResponse<ProfileContact>> get(
            @PathVariable String id, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "profile_contacts-get", "GET", BASE + "/{id}", headers, null, Map.of("id", id));
        try {
            ProfileContact data = service.findById(id, call.context());
            call.trace().success(Map.of("id", id));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:contacts:create")
    public ResponseEntity<ApiResponse<ProfileContact>> create(
            @RequestBody ProfileContact body, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "profile_contacts-create", "POST", BASE, headers, body, null);
        try {
            ProfileContact created = service.create(body, call.context());
            call.trace().success(Map.of("id", created.getId()));
            return createdBody(created, "ProfileContact created", call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:contacts:update")
    public ResponseEntity<ApiResponse<ProfileContact>> update(
            @PathVariable String id, @RequestBody ProfileContact body, @ClientHeaders ClientHeadersPayload headers) {
        body.setId(id);
        TracedCall call = tracedCall(log, "profile_contacts-update", "PUT", BASE + "/{id}", headers, body, Map.of("id", id));
        try {
            ProfileContact updated = service.update(body, call.context());
            call.trace().success(Map.of("id", id));
            return ok(updated, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @RequirePermission("staffiq:contacts:delete")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable String id, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "profile_contacts-delete", "DELETE", BASE + "/{id}", headers, null, Map.of("id", id));
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
