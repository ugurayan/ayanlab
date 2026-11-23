package com.ayanlab.hrm.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.annotation.RequirePermission;
import com.ayanlab.core.controller.BaseController;
import com.ayanlab.core.data.ClientHeadersPayload;
import com.ayanlab.core.data.responses.ApiResponse;
import com.ayanlab.hrm.backend.entity.ContactType;
import com.ayanlab.hrm.backend.service.ContactTypeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/hrm/api/contact-types")
@RequiredArgsConstructor
@Tag(name = "HRM · ContactType", description = "ContactType CRUD")
public class ContactTypeController extends BaseController {

    private static final String BASE = "/hrm/api/contact-types";
    private final ContactTypeService service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:contact_types:read")
    public ResponseEntity<ApiResponse<List<ContactType>>> list(@ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "contact_types-list", "GET", BASE, headers, null, null);
        try {
            List<ContactType> data = service.findAll(call.context());
            call.trace().success(Map.of("count", data.size()));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:contact_types:read")
    public ResponseEntity<ApiResponse<ContactType>> get(
            @PathVariable String id, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "contact_types-get", "GET", BASE + "/{id}", headers, null, Map.of("id", id));
        try {
            ContactType data = service.findById(id, call.context());
            call.trace().success(Map.of("id", id));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:contact_types:create")
    public ResponseEntity<ApiResponse<ContactType>> create(
            @RequestBody ContactType body, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "contact_types-create", "POST", BASE, headers, body, null);
        try {
            ContactType created = service.create(body, call.context());
            call.trace().success(Map.of("id", created.getId()));
            return createdBody(created, "ContactType created", call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequirePermission("staffiq:contact_types:update")
    public ResponseEntity<ApiResponse<ContactType>> update(
            @PathVariable String id, @RequestBody ContactType body, @ClientHeaders ClientHeadersPayload headers) {
        body.setId(id);
        TracedCall call = tracedCall(log, "contact_types-update", "PUT", BASE + "/{id}", headers, body, Map.of("id", id));
        try {
            ContactType updated = service.update(body, call.context());
            call.trace().success(Map.of("id", id));
            return ok(updated, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @RequirePermission("staffiq:contact_types:delete")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable String id, @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "contact_types-delete", "DELETE", BASE + "/{id}", headers, null, Map.of("id", id));
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
