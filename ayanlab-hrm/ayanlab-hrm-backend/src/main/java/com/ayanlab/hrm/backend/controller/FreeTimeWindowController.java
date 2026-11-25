package com.ayanlab.hrm.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.annotation.RequirePermission;
import com.ayanlab.core.controller.BaseController;
import com.ayanlab.core.dto.ClientHeadersPayload;
import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.hrm.backend.entity.FreeTimeWindow;
import com.ayanlab.hrm.backend.service.FreeTimeWindowService;
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
@RequestMapping("/hrm/api/free-time-windows")
@RequiredArgsConstructor
@Tag(name = "HRM · FreeTimeWindow", description = "FreeTimeWindow CRUD")
public class FreeTimeWindowController extends BaseController {

    private static final String BASE = "/hrm/api/free-time-windows";
    private final FreeTimeWindowService service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:free_time_windows:read")
    public ResponseEntity<ApiResponse<List<FreeTimeWindow>>> list(@ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "free_time_windows-list", "GET", BASE, headers, null, null);
        try {
            List<FreeTimeWindow> data = service.findAll(call.context());
            call.trace().success(Map.of("count", data.size()));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:free_time_windows:read")
    public ResponseEntity<ApiResponse<FreeTimeWindow>> get(
            @PathVariable UUID id, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "free_time_windows-get", "GET", BASE + "/{id}", headers, null, Map.of("id", id));
        try {
            FreeTimeWindow data = service.findById(id, call.context());
            call.trace().success(Map.of("id", id));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:free_time_windows:create")
    public ResponseEntity<ApiResponse<FreeTimeWindow>> create(
            @RequestBody FreeTimeWindow body, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "free_time_windows-create", "POST", BASE, headers, body, null);
        try {
            FreeTimeWindow created = service.create(body, call.context());
            call.trace().success(Map.of("id", created.getId()));
            return createdBody(created, "FreeTimeWindow created", call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:free_time_windows:update")
    public ResponseEntity<ApiResponse<FreeTimeWindow>> update(
            @PathVariable UUID id, @RequestBody FreeTimeWindow body, @ClientHeaders ClientHeadersPayload headers) {
        body.setId(id);
        TracedCall call = tracedCall(log, "free_time_windows-update", "PUT", BASE + "/{id}", headers, body, Map.of("id", id));
        try {
            FreeTimeWindow updated = service.update(body, call.context());
            call.trace().success(Map.of("id", id));
            return ok(updated, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @RequirePermission("staffiq:free_time_windows:delete")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable UUID id, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "free_time_windows-delete", "DELETE", BASE + "/{id}", headers, null, Map.of("id", id));
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
