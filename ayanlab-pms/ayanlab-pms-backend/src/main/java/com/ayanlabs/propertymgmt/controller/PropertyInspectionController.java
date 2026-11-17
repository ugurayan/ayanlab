package com.ayanlabs.propertymgmt.controller;

import com.ayanlabs.core.annotation.ClientHeaders;
import com.ayanlabs.core.data.ClientHeadersPayload;
import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.propertymgmt.entity.inspections.PropertyInspection;
import com.ayanlabs.propertymgmt.service.IPropertyInspectionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pms/api/property-inspections")
@Tag(name = "IPMS · PropertyInspectionController", description = "PropertyInspection CRUD")
@RequiredArgsConstructor
public class PropertyInspectionController extends IpmsCrudController<PropertyInspection> {

    private static final String BASE_PATH = "/pms/api/property-inspections";
    private final IPropertyInspectionService propertyInspectionService;

    @Override
    protected IPropertyInspectionService service() {
        return propertyInspectionService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "property-inspections";
    }

    @Override
    protected String entityLabel() {
        return "PropertyInspection";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<PropertyInspection>>> list(
            @ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }
}
