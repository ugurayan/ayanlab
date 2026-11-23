package com.ayanlab.hrm.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.annotation.RequirePermission;
import com.ayanlab.core.controller.BaseController;
import com.ayanlab.core.data.ClientHeadersPayload;
import com.ayanlab.core.data.responses.ApiResponse;
import com.ayanlab.hrm.backend.entity.Location;
import com.ayanlab.hrm.backend.service.LocationService;
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
@RequestMapping("/hrm/api/locations")
@RequiredArgsConstructor
@Tag(name = "HRM · Location", description = "Location CRUD")
public class LocationController extends BaseController {

    private static final String BASE = "/hrm/api/locations";
    private final LocationService service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:locations:read")
    public ResponseEntity<ApiResponse<List<Location>>> list(@ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "locations-list", "GET", BASE, headers, null, null);
        try {
            List<Location> data = service.findAll(call.context());
            call.trace().success(Map.of("count", data.size()));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:locations:read")
    public ResponseEntity<ApiResponse<Location>> get(
            @PathVariable String id, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "locations-get", "GET", BASE + "/{id}", headers, null, Map.of("id", id));
        try {
            Location data = service.findById(id, call.context());
            call.trace().success(Map.of("id", id));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:locations:create")
    public ResponseEntity<ApiResponse<Location>> create(
            @RequestBody Location body, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "locations-create", "POST", BASE, headers, body, null);
        try {
            Location created = service.create(body, call.context());
            call.trace().success(Map.of("id", created.getId()));
            return createdBody(created, "Location created", call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:locations:update")
    public ResponseEntity<ApiResponse<Location>> update(
            @PathVariable String id, @RequestBody Location body, @ClientHeaders ClientHeadersPayload headers) {
        body.setId(id);
        TracedCall call = tracedCall(log, "locations-update", "PUT", BASE + "/{id}", headers, body, Map.of("id", id));
        try {
            Location updated = service.update(body, call.context());
            call.trace().success(Map.of("id", id));
            return ok(updated, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @RequirePermission("staffiq:locations:delete")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable String id, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "locations-delete", "DELETE", BASE + "/{id}", headers, null, Map.of("id", id));
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
