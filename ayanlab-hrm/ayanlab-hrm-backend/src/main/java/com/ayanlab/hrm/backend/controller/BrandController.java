package com.ayanlab.hrm.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.annotation.RequirePermission;
import com.ayanlab.core.controller.BaseController;
import com.ayanlab.core.dto.ClientHeadersPayload;
import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.hrm.backend.entity.Brand;
import com.ayanlab.hrm.backend.service.BrandService;
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
@RequestMapping("/hrm/api/brands")
@RequiredArgsConstructor
@Tag(name = "HRM · Brand", description = "Brand CRUD")
public class BrandController extends BaseController {

    private static final String BASE = "/hrm/api/brands";
    private final BrandService service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:brands:read")
    public ResponseEntity<ApiResponse<List<Brand>>> list(@ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "brands-list", "GET", BASE, headers, null, null);
        try {
            List<Brand> data = service.findAll(call.context());
            call.trace().success(Map.of("count", data.size()));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:brands:read")
    public ResponseEntity<ApiResponse<Brand>> get(
            @PathVariable UUID id, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "brands-get", "GET", BASE + "/{id}", headers, null, Map.of("id", id));
        try {
            Brand data = service.findById(id, call.context());
            call.trace().success(Map.of("id", id));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:brands:create")
    public ResponseEntity<ApiResponse<Brand>> create(
            @RequestBody Brand body, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "brands-create", "POST", BASE, headers, body, null);
        try {
            Brand created = service.create(body, call.context());
            call.trace().success(Map.of("id", created.getId()));
            return createdBody(created, "Brand created", call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:brands:update")
    public ResponseEntity<ApiResponse<Brand>> update(
            @PathVariable UUID id, @RequestBody Brand body, @ClientHeaders ClientHeadersPayload headers) {
        body.setId(id);
        TracedCall call = tracedCall(log, "brands-update", "PUT", BASE + "/{id}", headers, body, Map.of("id", id));
        try {
            Brand updated = service.update(body, call.context());
            call.trace().success(Map.of("id", id));
            return ok(updated, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @RequirePermission("staffiq:brands:delete")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable UUID id, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "brands-delete", "DELETE", BASE + "/{id}", headers, null, Map.of("id", id));
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
