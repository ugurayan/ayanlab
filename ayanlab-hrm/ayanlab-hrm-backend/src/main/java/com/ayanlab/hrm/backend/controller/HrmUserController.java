package com.ayanlab.hrm.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.annotation.RequirePermission;
import com.ayanlab.core.controller.BaseController;
import com.ayanlab.core.dto.ClientHeadersPayload;
import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.hrm.backend.entity.HrmUser;
import com.ayanlab.hrm.backend.service.HrmUserService;
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
@RequestMapping("/hrm/api/users")
@RequiredArgsConstructor
@Tag(name = "HRM · HrmUser", description = "HrmUser CRUD")
public class HrmUserController extends BaseController {

    private static final String BASE = "/hrm/api/users";
    private final HrmUserService service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:settings:read")
    public ResponseEntity<ApiResponse<List<HrmUser>>> list(@ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "users-list", "GET", BASE, headers, null, null);
        try {
            List<HrmUser> data = service.findAll(call.context());
            call.trace().success(Map.of("count", data.size()));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:settings:read")
    public ResponseEntity<ApiResponse<HrmUser>> get(
            @PathVariable UUID id, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "users-get", "GET", BASE + "/{id}", headers, null, Map.of("id", id));
        try {
            HrmUser data = service.findById(id, call.context());
            call.trace().success(Map.of("id", id));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:settings:create")
    public ResponseEntity<ApiResponse<HrmUser>> create(
            @RequestBody HrmUser body, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "users-create", "POST", BASE, headers, body, null);
        try {
            HrmUser created = service.create(body, call.context());
            call.trace().success(Map.of("id", created.getId()));
            return createdBody(created, "HrmUser created", call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:settings:update")
    public ResponseEntity<ApiResponse<HrmUser>> update(
            @PathVariable UUID id, @RequestBody HrmUser body, @ClientHeaders ClientHeadersPayload headers) {
        body.setId(id);
        TracedCall call = tracedCall(log, "users-update", "PUT", BASE + "/{id}", headers, body, Map.of("id", id));
        try {
            HrmUser updated = service.update(body, call.context());
            call.trace().success(Map.of("id", id));
            return ok(updated, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @RequirePermission("staffiq:settings:delete")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable UUID id, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "users-delete", "DELETE", BASE + "/{id}", headers, null, Map.of("id", id));
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
