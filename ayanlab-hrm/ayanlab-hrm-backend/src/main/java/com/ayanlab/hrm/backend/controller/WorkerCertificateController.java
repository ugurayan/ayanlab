package com.ayanlab.hrm.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.annotation.RequirePermission;
import com.ayanlab.core.controller.BaseController;
import com.ayanlab.core.dto.ClientHeadersPayload;
import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.hrm.backend.entity.WorkerCertificate;
import com.ayanlab.hrm.backend.service.WorkerCertificateService;
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
@RequestMapping("/hrm/api/worker-certificates")
@RequiredArgsConstructor
@Tag(name = "HRM · WorkerCertificate", description = "WorkerCertificate CRUD")
public class WorkerCertificateController extends BaseController {

    private static final String BASE = "/hrm/api/worker-certificates";
    private final WorkerCertificateService service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:worker_certificates:read")
    public ResponseEntity<ApiResponse<List<WorkerCertificate>>> list(@ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "worker_certificates-list", "GET", BASE, headers, null, null);
        try {
            List<WorkerCertificate> data = service.findAll(call.context());
            call.trace().success(Map.of("count", data.size()));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:worker_certificates:read")
    public ResponseEntity<ApiResponse<WorkerCertificate>> get(
            @PathVariable UUID id, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "worker_certificates-get", "GET", BASE + "/{id}", headers, null, Map.of("id", id));
        try {
            WorkerCertificate data = service.findById(id, call.context());
            call.trace().success(Map.of("id", id));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:worker_certificates:create")
    public ResponseEntity<ApiResponse<WorkerCertificate>> create(
            @RequestBody WorkerCertificate body, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "worker_certificates-create", "POST", BASE, headers, body, null);
        try {
            WorkerCertificate created = service.create(body, call.context());
            call.trace().success(Map.of("id", created.getId()));
            return createdBody(created, "WorkerCertificate created", call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:worker_certificates:update")
    public ResponseEntity<ApiResponse<WorkerCertificate>> update(
            @PathVariable UUID id, @RequestBody WorkerCertificate body, @ClientHeaders ClientHeadersPayload headers) {
        body.setId(id);
        TracedCall call = tracedCall(log, "worker_certificates-update", "PUT", BASE + "/{id}", headers, body, Map.of("id", id));
        try {
            WorkerCertificate updated = service.update(body, call.context());
            call.trace().success(Map.of("id", id));
            return ok(updated, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @RequirePermission("staffiq:worker_certificates:delete")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable UUID id, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "worker_certificates-delete", "DELETE", BASE + "/{id}", headers, null, Map.of("id", id));
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
