package com.ayanlab.pms.backend.controller;

import com.ayanlab.pms.backend.entity.leasing.Lease;
import com.ayanlab.pms.backend.service.ILeaseService;
import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.dto.ClientHeadersPayload;
import com.ayanlab.core.dto.response.ApiResponse;
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
@RequestMapping("/pms/api/leases")
@Tag(name = "IPMS · LeaseController", description = "Lease management (Buildium-style CRUD)")
@RequiredArgsConstructor
public class LeaseController extends IpmsCrudController<Lease> {

    private static final String BASE_PATH = "/pms/api/leases";
    private final ILeaseService leaseService;

    @Override
    protected ILeaseService service() {
        return leaseService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "leases";
    }

    @Override
    protected String entityLabel() {
        return "Lease";
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<Lease>>> list(
            @RequestParam(value = "propertyId", required = false) UUID propertyId,
            @RequestParam(value = "unitId", required = false) UUID unitId,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> traceParams = new LinkedHashMap<>();
        traceParams.put("propertyId", propertyId);
        traceParams.put("unitId", unitId);
        return executeList(headers, traceParams, ctx -> ((ILeaseService) service()).findAll(ctx, propertyId, unitId));
    }

}
