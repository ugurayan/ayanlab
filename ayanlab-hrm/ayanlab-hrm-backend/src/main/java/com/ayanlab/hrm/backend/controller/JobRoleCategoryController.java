package com.ayanlab.hrm.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.annotation.RequirePermission;
import com.ayanlab.core.controller.BaseController;
import com.ayanlab.core.dto.ClientHeadersPayload;
import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.hrm.backend.entity.JobRoleCategory;
import com.ayanlab.hrm.backend.service.JobRoleCategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.LinkedHashMap;
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
@RequestMapping("/hrm/api/job-role-categories")
@RequiredArgsConstructor
@Tag(name = "HRM · JobRoleCategory", description = "JobRoleCategory CRUD")
public class JobRoleCategoryController extends BaseController {

    private static final String BASE = "/hrm/api/job-role-categories";
    private final JobRoleCategoryService service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:industries:read")
    public ResponseEntity<ApiResponse<List<JobRoleCategory>>> list(@ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "job_role_categories-list", "GET", BASE, headers, null, null);
        try {
            List<JobRoleCategory> data = service.findAll(call.context());
            call.trace().success(Map.of("count", data.size()));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:industries:read")
    public ResponseEntity<ApiResponse<JobRoleCategory>> get(
            @PathVariable UUID id, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "job_role_categories-get", "GET", BASE + "/{id}", headers, null, Map.of("id", id));
        try {
            JobRoleCategory data = service.findById(id, call.context());
            call.trace().success(Map.of("id", id));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:industries:create")
    public ResponseEntity<ApiResponse<JobRoleCategory>> create(
            @RequestBody JobRoleCategory body, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "job_role_categories-create", "POST", BASE, headers, body, null);
        try {
            JobRoleCategory created = service.create(body, call.context());
            call.trace().success(Map.of("id", created.getId()));
            return createdBody(created, "JobRoleCategory created", call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:industries:update")
    public ResponseEntity<ApiResponse<JobRoleCategory>> update(
            @PathVariable UUID id, @RequestBody JobRoleCategory body, @ClientHeaders ClientHeadersPayload headers) {
        body.setId(id);
        TracedCall call = tracedCall(log, "job_role_categories-update", "PUT", BASE + "/{id}", headers, body, Map.of("id", id));
        try {
            JobRoleCategory updated = service.update(body, call.context());
            call.trace().success(Map.of("id", id));
            return ok(updated, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @RequirePermission("staffiq:industries:delete")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable UUID id, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "job_role_categories-delete", "DELETE", BASE + "/{id}", headers, null, Map.of("id", id));
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
