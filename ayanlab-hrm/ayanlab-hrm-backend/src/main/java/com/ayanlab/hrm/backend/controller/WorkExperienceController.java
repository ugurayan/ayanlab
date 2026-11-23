package com.ayanlab.hrm.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.annotation.RequirePermission;
import com.ayanlab.core.controller.BaseController;
import com.ayanlab.core.data.ClientHeadersPayload;
import com.ayanlab.core.data.responses.ApiResponse;
import com.ayanlab.hrm.backend.entity.WorkExperience;
import com.ayanlab.hrm.backend.service.WorkExperienceService;
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
@RequestMapping("/hrm/api/work-experiences")
@RequiredArgsConstructor
@Tag(name = "HRM · WorkExperience", description = "WorkExperience CRUD")
public class WorkExperienceController extends BaseController {

    private static final String BASE = "/hrm/api/work-experiences";
    private final WorkExperienceService service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:experiences:read")
    public ResponseEntity<ApiResponse<List<WorkExperience>>> list(@ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "work_experiences-list", "GET", BASE, headers, null, null);
        try {
            List<WorkExperience> data = service.findAll(call.context());
            call.trace().success(Map.of("count", data.size()));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:experiences:read")
    public ResponseEntity<ApiResponse<WorkExperience>> get(
            @PathVariable String id, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "work_experiences-get", "GET", BASE + "/{id}", headers, null, Map.of("id", id));
        try {
            WorkExperience data = service.findById(id, call.context());
            call.trace().success(Map.of("id", id));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:experiences:create")
    public ResponseEntity<ApiResponse<WorkExperience>> create(
            @RequestBody WorkExperience body, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "work_experiences-create", "POST", BASE, headers, body, null);
        try {
            WorkExperience created = service.create(body, call.context());
            call.trace().success(Map.of("id", created.getId()));
            return createdBody(created, "WorkExperience created", call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:experiences:update")
    public ResponseEntity<ApiResponse<WorkExperience>> update(
            @PathVariable String id, @RequestBody WorkExperience body, @ClientHeaders ClientHeadersPayload headers) {
        body.setId(id);
        TracedCall call = tracedCall(log, "work_experiences-update", "PUT", BASE + "/{id}", headers, body, Map.of("id", id));
        try {
            WorkExperience updated = service.update(body, call.context());
            call.trace().success(Map.of("id", id));
            return ok(updated, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @RequirePermission("staffiq:experiences:delete")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable String id, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "work_experiences-delete", "DELETE", BASE + "/{id}", headers, null, Map.of("id", id));
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
