package com.ayanlab.pms.backend.controller;

import com.ayanlab.pms.backend.entity.rentals.PropertyUnit;
import com.ayanlab.pms.backend.service.IPropertyUnitService;
import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.data.ClientHeadersPayload;
import com.ayanlab.core.data.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pms/api/property-units")
@Tag(name = "IPMS · PropertyUnitController", description = "PropertyUnit management (Buildium-style CRUD)")
@RequiredArgsConstructor
public class PropertyUnitController extends IpmsCrudController<PropertyUnit> {

    private static final String BASE_PATH = "/pms/api/property-units";
    private final IPropertyUnitService propertyUnitService;

    @Override
    protected IPropertyUnitService service() {
        return propertyUnitService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "property-units";
    }

    @Override
    protected String entityLabel() {
        return "PropertyUnit";
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<PropertyUnit>>> list(
            @RequestParam(value = "propertyId", required = false) UUID propertyId,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> traceParams = new LinkedHashMap<>();
        traceParams.put("propertyId", propertyId);
        return executeList(headers, traceParams, ctx -> ((IPropertyUnitService) service()).findAll(ctx, propertyId));
    }

}
