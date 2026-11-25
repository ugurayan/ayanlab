package com.ayanlab.hrm.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.annotation.RequirePermission;
import com.ayanlab.core.controller.BaseController;
import com.ayanlab.core.dto.ClientHeadersPayload;
import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.hrm.backend.entity.LegalEntity;
import com.ayanlab.hrm.backend.service.LegalEntityService;
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
@RequestMapping("/hrm/api/legal-entities")
@RequiredArgsConstructor
@Tag(name = "HRM · LegalEntity", description = "LegalEntity CRUD")
public class LegalEntityController extends BaseController {

    private static final String BASE = "/hrm/api/legal-entities";
    private final LegalEntityService service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:legal_entities:read")
    public ResponseEntity<ApiResponse<List<LegalEntity>>> list(@ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "legal-entities-list", "GET", BASE, headers, null, null);
        try {
            List<LegalEntity> data = service.findAll(call.context());
            call.trace().success(Map.of("count", data.size()));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:legal_entities:read")
    public ResponseEntity<ApiResponse<LegalEntity>> get(
            @PathVariable UUID id, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "legal-entities-get", "GET", BASE + "/{id}", headers, null, Map.of("id", id));
        try {
            LegalEntity data = service.findById(id, call.context());
            call.trace().success(Map.of("id", id));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:legal_entities:create")
    public ResponseEntity<ApiResponse<LegalEntity>> create(
            @RequestBody LegalEntity body, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "legal-entities-create", "POST", BASE, headers, body, null);
        try {
            LegalEntity created = service.create(body, call.context());
            call.trace().success(Map.of("id", created.getId()));
            return createdBody(created, "LegalEntity created", call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:legal_entities:update")
    public ResponseEntity<ApiResponse<LegalEntity>> update(
            @PathVariable UUID id, @RequestBody LegalEntity body, @ClientHeaders ClientHeadersPayload headers) {
        body.setId(id);
        TracedCall call = tracedCall(log, "legal-entities-update", "PUT", BASE + "/{id}", headers, body, Map.of("id", id));
        try {
            LegalEntity updated = service.update(body, call.context());
            call.trace().success(Map.of("id", id));
            return ok(updated, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @RequirePermission("staffiq:legal_entities:delete")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable UUID id, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "legal-entities-delete", "DELETE", BASE + "/{id}", headers, null, Map.of("id", id));
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
