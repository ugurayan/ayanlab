package com.ayanlab.hrm.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.annotation.RequirePermission;
import com.ayanlab.core.controller.BaseController;
import com.ayanlab.core.dto.ClientHeadersPayload;
import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.hrm.backend.entity.ExpertiseLevel;
import com.ayanlab.hrm.backend.service.ExpertiseLevelService;
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
@RequestMapping("/hrm/api/expertise-levels")
@RequiredArgsConstructor
@Tag(name = "HRM · ExpertiseLevel", description = "ExpertiseLevel CRUD")
public class ExpertiseLevelController extends BaseController {

    private static final String BASE = "/hrm/api/expertise-levels";
    private final ExpertiseLevelService service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:expertise_levels:read")
    public ResponseEntity<ApiResponse<List<ExpertiseLevel>>> list(@ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "expertise_levels-list", "GET", BASE, headers, null, null);
        try {
            List<ExpertiseLevel> data = service.findAll(call.context());
            call.trace().success(Map.of("count", data.size()));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:expertise_levels:read")
    public ResponseEntity<ApiResponse<ExpertiseLevel>> get(
            @PathVariable UUID id, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "expertise_levels-get", "GET", BASE + "/{id}", headers, null, Map.of("id", id));
        try {
            ExpertiseLevel data = service.findById(id, call.context());
            call.trace().success(Map.of("id", id));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:expertise_levels:create")
    public ResponseEntity<ApiResponse<ExpertiseLevel>> create(
            @RequestBody ExpertiseLevel body, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "expertise_levels-create", "POST", BASE, headers, body, null);
        try {
            ExpertiseLevel created = service.create(body, call.context());
            call.trace().success(Map.of("id", created.getId()));
            return createdBody(created, "ExpertiseLevel created", call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:expertise_levels:update")
    public ResponseEntity<ApiResponse<ExpertiseLevel>> update(
            @PathVariable UUID id, @RequestBody ExpertiseLevel body, @ClientHeaders ClientHeadersPayload headers) {
        body.setId(id);
        TracedCall call = tracedCall(log, "expertise_levels-update", "PUT", BASE + "/{id}", headers, body, Map.of("id", id));
        try {
            ExpertiseLevel updated = service.update(body, call.context());
            call.trace().success(Map.of("id", id));
            return ok(updated, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @RequirePermission("staffiq:expertise_levels:delete")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable UUID id, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "expertise_levels-delete", "DELETE", BASE + "/{id}", headers, null, Map.of("id", id));
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
