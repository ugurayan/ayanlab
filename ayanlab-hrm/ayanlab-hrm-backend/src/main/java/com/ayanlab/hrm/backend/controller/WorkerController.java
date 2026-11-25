package com.ayanlab.hrm.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.annotation.RequirePermission;
import com.ayanlab.core.controller.BaseController;
import com.ayanlab.core.dto.ClientHeadersPayload;
import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.hrm.backend.entity.Worker;
import com.ayanlab.hrm.backend.service.WorkerService;
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
@RequestMapping("/hrm/api/workers")
@RequiredArgsConstructor
@Tag(name = "HRM · Worker", description = "Worker CRUD")
public class WorkerController extends BaseController {

    private static final String BASE = "/hrm/api/workers";
    private final WorkerService service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:workers:read")
    public ResponseEntity<ApiResponse<List<Worker>>> list(@ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "workers-list", "GET", BASE, headers, null, null);
        try {
            List<Worker> data = service.findAll(call.context());
            call.trace().success(Map.of("count", data.size()));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:workers:read")
    public ResponseEntity<ApiResponse<Worker>> get(
            @PathVariable UUID id, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "workers-get", "GET", BASE + "/{id}", headers, null, Map.of("id", id));
        try {
            Worker data = service.findById(id, call.context());
            call.trace().success(Map.of("id", id));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:workers:create")
    public ResponseEntity<ApiResponse<Worker>> create(
            @RequestBody Worker body, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "workers-create", "POST", BASE, headers, body, null);
        try {
            Worker created = service.create(body, call.context());
            call.trace().success(Map.of("id", created.getId()));
            return createdBody(created, "Worker created", call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:workers:update")
    public ResponseEntity<ApiResponse<Worker>> update(
            @PathVariable UUID id, @RequestBody Worker body, @ClientHeaders ClientHeadersPayload headers) {
        body.setId(id);
        TracedCall call = tracedCall(log, "workers-update", "PUT", BASE + "/{id}", headers, body, Map.of("id", id));
        try {
            Worker updated = service.update(body, call.context());
            call.trace().success(Map.of("id", id));
            return ok(updated, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @RequirePermission("staffiq:workers:delete")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable UUID id, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "workers-delete", "DELETE", BASE + "/{id}", headers, null, Map.of("id", id));
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
