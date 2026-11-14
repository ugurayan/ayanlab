package com.ayanlabs.core.controller;

import com.ayanlabs.core.data.ClientHeadersPayload;

import com.ayanlabs.core.annotation.ClientHeaders;

import com.ayanlabs.core.data.enums.NotificationTypeEnum;
import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.core.data.responses.PagedResponse;
import com.ayanlabs.core.entity.NotificationTemplate;
import com.ayanlabs.core.service.INotificationTemplateService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/core/templates")
@Tag(name = "Core · NotificationTemplateController", description = "Notification templates")
@RequiredArgsConstructor
public class NotificationTemplateController extends BaseController {

    private static final String BASE_PATH = "/api/core/templates";

    private final INotificationTemplateService templateService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<NotificationTemplate>> create(
            @Valid @RequestBody NotificationTemplate template,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        TracedCall call = tracedCall(log, "create-template", "POST", BASE_PATH, headers, template, null);
        try {
            NotificationTemplate created = templateService.create(template, call.context());
            ResponseEntity<ApiResponse<NotificationTemplate>> response = ok(created, call.context());
            call.trace().success(Map.of("createdId", created != null ? created.getId() : null, "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<NotificationTemplate>> update(
            @PathVariable UUID id,
            @Valid @RequestBody NotificationTemplate template,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        TracedCall call = tracedCall(log, "update-template", "PUT", BASE_PATH + "/{id}", headers, template, params);
        try {
            template.setId(id);
            NotificationTemplate updated = templateService.update(template, call.context());
            ResponseEntity<ApiResponse<NotificationTemplate>> response = ok(updated, call.context());
            call.trace().success(Map.of("updatedId", updated != null ? updated.getId() : null, "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<NotificationTemplate>> getById(
            @PathVariable UUID id,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        TracedCall call = tracedCall(log, "get-template", "GET", BASE_PATH + "/{id}", headers, null, params);
        try {
            NotificationTemplate found = templateService.findById(id, call.context());
            ResponseEntity<ApiResponse<NotificationTemplate>> response = ok(found, call.context());
            call.trace().success(Map.of("foundId", found != null ? found.getId() : null, "success", found != null));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<ApiResponse<Object>> deleteById(
            @PathVariable UUID id,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        TracedCall call = tracedCall(log, "delete-template", "DELETE", BASE_PATH + "/{id}", headers, null, params);
        try {
            templateService.deleteById(id, call.context());
            ResponseEntity<ApiResponse<Object>> response = ok(null, call.context());
            call.trace().success(Map.of("deletedId", id, "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<PagedResponse<NotificationTemplate>>> list(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "20") int size,
            @RequestParam(value = "appId", required = false) UUID appIdParam,
            @RequestParam(value = "type", required = false) NotificationTypeEnum type,
            @RequestParam(value = "code", required = false) String code,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("page", page);
        params.put("size", size);
        params.put("appId", appIdParam);
        params.put("type", type);
        params.put("code", code);
        TracedCall call = tracedCall(log, "list-templates", "GET", BASE_PATH, headers, null, params);
        try {
            PagedResponse<NotificationTemplate> paged = templateService.findAll(page, size, appIdParam, type, code, call.context());
            ResponseEntity<ApiResponse<PagedResponse<NotificationTemplate>>> response = ok(paged, call.context());
            call.trace().success(Map.of("count", paged != null && paged.getContent() != null ? paged.getContent().size() : 0, "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }
}
