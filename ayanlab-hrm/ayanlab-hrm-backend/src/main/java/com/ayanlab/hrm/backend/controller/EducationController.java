package com.ayanlab.hrm.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.annotation.RequirePermission;
import com.ayanlab.core.controller.BaseController;
import com.ayanlab.core.data.ClientHeadersPayload;
import com.ayanlab.core.data.responses.ApiResponse;
import com.ayanlab.hrm.backend.entity.Education;
import com.ayanlab.hrm.backend.service.EducationService;
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
@RequestMapping("/hrm/api/educations")
@RequiredArgsConstructor
@Tag(name = "HRM · Education", description = "Education CRUD")
public class EducationController extends BaseController {

    private static final String BASE = "/hrm/api/educations";
    private final EducationService service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:education:read")
    public ResponseEntity<ApiResponse<List<Education>>> list(@ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "educations-list", "GET", BASE, headers, null, null);
        try {
            List<Education> data = service.findAll(call.context());
            call.trace().success(Map.of("count", data.size()));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:education:read")
    public ResponseEntity<ApiResponse<Education>> get(
            @PathVariable String id, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "educations-get", "GET", BASE + "/{id}", headers, null, Map.of("id", id));
        try {
            Education data = service.findById(id, call.context());
            call.trace().success(Map.of("id", id));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:education:create")
    public ResponseEntity<ApiResponse<Education>> create(
            @RequestBody Education body, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "educations-create", "POST", BASE, headers, body, null);
        try {
            Education created = service.create(body, call.context());
            call.trace().success(Map.of("id", created.getId()));
            return createdBody(created, "Education created", call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:education:update")
    public ResponseEntity<ApiResponse<Education>> update(
            @PathVariable String id, @RequestBody Education body, @ClientHeaders ClientHeadersPayload headers) {
        body.setId(id);
        TracedCall call = tracedCall(log, "educations-update", "PUT", BASE + "/{id}", headers, body, Map.of("id", id));
        try {
            Education updated = service.update(body, call.context());
            call.trace().success(Map.of("id", id));
            return ok(updated, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @RequirePermission("staffiq:education:delete")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable String id, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "educations-delete", "DELETE", BASE + "/{id}", headers, null, Map.of("id", id));
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
