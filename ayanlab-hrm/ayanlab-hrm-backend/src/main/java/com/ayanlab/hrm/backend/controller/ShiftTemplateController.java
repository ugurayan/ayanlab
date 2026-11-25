package com.ayanlab.hrm.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.annotation.RequirePermission;
import com.ayanlab.core.controller.BaseController;
import com.ayanlab.core.dto.ClientHeadersPayload;
import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.hrm.backend.entity.ShiftTemplate;
import com.ayanlab.hrm.backend.service.ShiftTemplateService;
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
@RequestMapping("/hrm/api/shift-templates")
@RequiredArgsConstructor
@Tag(name = "HRM · Shift template", description = "ShiftTemplate CRUD")
public class ShiftTemplateController extends BaseController {

    private static final String BASE = "/hrm/api/shift-templates";
    private final ShiftTemplateService service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:shift_templates:read")
    public ResponseEntity<ApiResponse<List<ShiftTemplate>>> list(@ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "shift-templates-list", "GET", BASE, headers, null, null);
        try {
            List<ShiftTemplate> data = service.findAll(call.context());
            call.trace().success(Map.of("count", data.size()));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:shift_templates:read")
    public ResponseEntity<ApiResponse<ShiftTemplate>> get(
            @PathVariable UUID id, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "shift-templates-get", "GET", BASE + "/{id}", headers, null, Map.of("id", id));
        try {
            ShiftTemplate data = service.findById(id, call.context());
            call.trace().success(Map.of("id", id));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:shift_templates:create")
    public ResponseEntity<ApiResponse<ShiftTemplate>> create(
            @RequestBody ShiftTemplate body, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "shift-templates-create", "POST", BASE, headers, body, null);
        try {
            ShiftTemplate created = service.create(body, call.context());
            call.trace().success(Map.of("id", created.getId()));
            return createdBody(created, "ShiftTemplate created", call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:shift_templates:update")
    public ResponseEntity<ApiResponse<ShiftTemplate>> update(
            @PathVariable UUID id, @RequestBody ShiftTemplate body, @ClientHeaders ClientHeadersPayload headers) {
        body.setId(id);
        TracedCall call = tracedCall(log, "shift-templates-update", "PUT", BASE + "/{id}", headers, body, Map.of("id", id));
        try {
            ShiftTemplate updated = service.update(body, call.context());
            call.trace().success(Map.of("id", id));
            return ok(updated, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }


    @PostMapping(value = "/{id}/instances", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:shift_templates:create")
    public ResponseEntity<ApiResponse<java.util.List<com.ayanlab.hrm.backend.entity.Shift>>> generate(
            @PathVariable UUID id,
            @RequestBody com.ayanlab.hrm.backend.dto.request.GenerateShiftInstancesRequest body,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "shift-templates-generate", "POST", BASE + "/{id}/instances", headers, body, java.util.Map.of("id", id));
        try {
            var created = service.generate(id, body, call.context());
            call.trace().success(java.util.Map.of("count", created.size()));
            return ok(created, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @RequirePermission("staffiq:shift_templates:delete")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable UUID id, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "shift-templates-delete", "DELETE", BASE + "/{id}", headers, null, Map.of("id", id));
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
