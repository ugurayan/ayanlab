package com.ayanlabs.propertymgmt.controller;

import com.ayanlabs.propertymgmt.entity.maintenance.WorkOrder;
import com.ayanlabs.propertymgmt.service.IWorkOrderService;
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
@RequestMapping("/ayanlabs/api/property-mgmt/work-orders")
@Tag(name = "IPMS · WorkOrderController", description = "WorkOrder management (Buildium-style CRUD)")
@RequiredArgsConstructor
public class WorkOrderController extends IpmsCrudController<WorkOrder> {

    private static final String BASE_PATH = "/ayanlabs/api/property-mgmt/work-orders";
    private final IWorkOrderService workOrderService;

    @Override
    protected IWorkOrderService service() {
        return workOrderService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "work-orders";
    }

    @Override
    protected String entityLabel() {
        return "WorkOrder";
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<WorkOrder>>> list(
            @RequestParam(value = "maintenanceRequestId", required = false) UUID maintenanceRequestId,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> traceParams = new LinkedHashMap<>();
        traceParams.put("maintenanceRequestId", maintenanceRequestId);
        return executeList(headers, traceParams, ctx -> ((IWorkOrderService) service()).findAll(ctx, maintenanceRequestId));
    }

}
