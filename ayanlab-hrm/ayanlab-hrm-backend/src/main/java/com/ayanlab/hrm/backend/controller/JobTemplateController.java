package com.ayanlab.hrm.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.annotation.RequirePermission;
import com.ayanlab.core.controller.BaseController;
import com.ayanlab.core.dto.ClientHeadersPayload;
import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.hrm.backend.entity.JobTemplate;
import com.ayanlab.hrm.backend.service.JobTemplateService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/hrm/api/job-templates")
@RequiredArgsConstructor
@Tag(name = "HRM · JobTemplate", description = "JobTemplate CRUD")
public class JobTemplateController extends BaseController {

    private static final String BASE = "/hrm/api/job-templates";
    private final JobTemplateService service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:job_templates:read")
    public ResponseEntity<ApiResponse<List<JobTemplate>>> list(@ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "job_templates-list", "GET", BASE, headers, null, null);
        try {
            List<JobTemplate> data = service.findAll(call.context());
            call.trace().success(Map.of("count", data.size()));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:job_templates:read")
    public ResponseEntity<ApiResponse<JobTemplate>> get(
            @PathVariable UUID id, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "job_templates-get", "GET", BASE + "/{id}", headers, null, Map.of("id", id));
        try {
            JobTemplate data = service.findById(id, call.context());
            call.trace().success(Map.of("id", id));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:job_templates:create")
    public ResponseEntity<ApiResponse<JobTemplate>> create(
            @RequestBody JobTemplate body, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "job_templates-create", "POST", BASE, headers, body, null);
        try {
            JobTemplate created = service.create(body, call.context());
            call.trace().success(Map.of("id", created.getId()));
            return createdBody(created, "JobTemplate created", call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:job_templates:update")
    public ResponseEntity<ApiResponse<JobTemplate>> update(
            @PathVariable UUID id, @RequestBody JobTemplate body, @ClientHeaders ClientHeadersPayload headers) {
        body.setId(id);
        TracedCall call = tracedCall(log, "job_templates-update", "PUT", BASE + "/{id}", headers, body, Map.of("id", id));
        try {
            JobTemplate updated = service.update(body, call.context());
            call.trace().success(Map.of("id", id));
            return ok(updated, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @RequirePermission("staffiq:job_templates:delete")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable UUID id, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "job_templates-delete", "DELETE", BASE + "/{id}", headers, null, Map.of("id", id));
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
