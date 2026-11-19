package com.ayanlab.pms.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.annotation.RequirePermission;
import com.ayanlab.core.controller.BaseController;
import com.ayanlab.core.data.ClientHeadersPayload;
import com.ayanlab.core.data.responses.ApiResponse;
import com.ayanlab.pms.backend.entity.platform.IpmsActivityEvent;
import com.ayanlab.pms.backend.service.IIpmsActivityEventService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/pms/api/activity-events")
@Tag(name = "IPMS · ActivityEventController", description = "Platform / org activity feed")
@RequirePermission("ipms:dashboard:read")
@RequiredArgsConstructor
public class ActivityEventController extends BaseController {

    private static final String BASE_PATH = "/pms/api/activity-events";

    private final IIpmsActivityEventService activityEventService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<IpmsActivityEvent>>> list(
            @RequestParam(value = "limit", required = false) Integer limit,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("limit", limit);
        TracedCall call = tracedCall(log, "activity-events-list", "GET", BASE_PATH, headers, null, params);
        try {
            List<IpmsActivityEvent> data = activityEventService.findRecent(call.context(), limit);
            call.trace().success(Map.of("count", data.size(), "success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<IpmsActivityEvent>> getById(
            @PathVariable UUID id,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "activity-events-get", "GET", BASE_PATH + "/{id}", headers, null, Map.of("id", id));
        try {
            IpmsActivityEvent data = activityEventService.findById(id, call.context());
            call.trace().success(Map.of("success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<IpmsActivityEvent>> create(
            @RequestBody IpmsActivityEvent body,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "activity-events-create", "POST", BASE_PATH, headers, body, null);
        try {
            IpmsActivityEvent created = activityEventService.create(body, call.context());
            call.trace().success(Map.of("createdId", created.getId(), "success", true));
            return createdBody(created, "Activity event created", call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<IpmsActivityEvent>> update(
            @PathVariable UUID id,
            @RequestBody IpmsActivityEvent body,
            @ClientHeaders ClientHeadersPayload headers) {
        body.setId(id);
        TracedCall call = tracedCall(log, "activity-events-update", "PUT", BASE_PATH + "/{id}", headers, body, Map.of("id", id));
        try {
            IpmsActivityEvent updated = activityEventService.update(body, call.context());
            call.trace().success(Map.of("success", true));
            return ok(updated, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable UUID id,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "activity-events-delete", "DELETE", BASE_PATH + "/{id}", headers, null, Map.of("id", id));
        try {
            activityEventService.deleteById(id, call.context());
            call.trace().success(Map.of("success", true));
            return ok(null, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }
}
