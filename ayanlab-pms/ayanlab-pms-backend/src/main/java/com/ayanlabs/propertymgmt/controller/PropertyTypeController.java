package com.ayanlabs.propertymgmt.controller;

import com.ayanlabs.core.annotation.ClientHeaders;
import com.ayanlabs.core.controller.BaseController;
import com.ayanlabs.core.data.ClientHeadersPayload;
import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.propertymgmt.entity.rentals.PropertyType;
import com.ayanlabs.propertymgmt.service.IPropertyTypeService;
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
@RequestMapping("/pms/api/property-types")
@Tag(name = "IPMS · PropertyTypeController", description = "Property type catalog (system defaults + org-specific)")
@RequiredArgsConstructor
public class PropertyTypeController extends BaseController {

    private static final String BASE_PATH = "/pms/api/property-types";
    private final IPropertyTypeService propertyTypeService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<PropertyType>>> list(
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "list-property-types", "GET", BASE_PATH, headers, null, null);
        try {
            List<PropertyType> data = propertyTypeService.findVisible(call.context());
            call.trace().success(Map.of("count", data.size(), "success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<PropertyType>> getById(
            @PathVariable UUID id,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        TracedCall call = tracedCall(log, "get-property-type", "GET", BASE_PATH + "/{id}", headers, null, params);
        try {
            PropertyType found = propertyTypeService.findById(id, call.context());
            call.trace().success(Map.of("foundId", found.getId(), "success", true));
            return ok(found, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<PropertyType>> create(
            @RequestBody PropertyType propertyType,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "create-property-type", "POST", BASE_PATH, headers, propertyType, null);
        try {
            PropertyType created = propertyTypeService.create(propertyType, call.context());
            call.trace().success(Map.of("createdId", created.getId(), "success", true));
            return createdBody(created, "Property type created", call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<PropertyType>> update(
            @PathVariable UUID id,
            @RequestBody PropertyType propertyType,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        propertyType.setId(id);
        TracedCall call = tracedCall(log, "update-property-type", "PUT", BASE_PATH + "/{id}", headers, propertyType, params);
        try {
            PropertyType updated = propertyTypeService.update(propertyType, call.context());
            call.trace().success(Map.of("updatedId", updated.getId(), "success", true));
            return ok(updated, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteById(
            @PathVariable UUID id,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "delete-property-type", "DELETE", BASE_PATH + "/{id}", headers, null, Map.of("id", id));
        try {
            propertyTypeService.deleteById(id, call.context());
            call.trace().success(Map.of("deletedId", id, "success", true));
            return ok(null, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }
}
