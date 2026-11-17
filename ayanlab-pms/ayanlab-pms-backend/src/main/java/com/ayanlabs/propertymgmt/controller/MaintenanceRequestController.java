package com.ayanlabs.propertymgmt.controller;

import com.ayanlabs.propertymgmt.entity.maintenance.MaintenanceRequest;
import com.ayanlabs.propertymgmt.service.IMaintenanceRequestService;
import com.ayanlabs.core.annotation.ClientHeaders;
import com.ayanlabs.core.data.ClientHeadersPayload;
import com.ayanlabs.core.data.responses.ApiResponse;
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
@RequestMapping("/pms/api/maintenance-requests")
@Tag(name = "IPMS · MaintenanceRequestController", description = "MaintenanceRequest management (Buildium-style CRUD)")
@RequiredArgsConstructor
public class MaintenanceRequestController extends IpmsCrudController<MaintenanceRequest> {

    private static final String BASE_PATH = "/pms/api/maintenance-requests";
    private final IMaintenanceRequestService maintenanceRequestService;

    @Override
    protected IMaintenanceRequestService service() {
        return maintenanceRequestService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "maintenance-requests";
    }

    @Override
    protected String entityLabel() {
        return "MaintenanceRequest";
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<MaintenanceRequest>>> list(
            @RequestParam(value = "propertyId", required = false) UUID propertyId,
            @RequestParam(value = "unitId", required = false) UUID unitId,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> traceParams = new LinkedHashMap<>();
        traceParams.put("propertyId", propertyId);
        traceParams.put("unitId", unitId);
        return executeList(headers, traceParams, ctx -> ((IMaintenanceRequestService) service()).findAll(ctx, propertyId, unitId));
    }

}
