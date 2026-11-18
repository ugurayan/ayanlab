package com.ayanlab.pms.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.data.ClientHeadersPayload;
import com.ayanlab.core.data.responses.ApiResponse;
import com.ayanlab.pms.backend.entity.leasing.Tenant;
import com.ayanlab.pms.backend.service.ITenantService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/pms/api/tenants")
@Tag(name = "IPMS · TenantController", description = "Tenant management (Buildium-style CRUD)")
@RequiredArgsConstructor
public class TenantController extends IpmsCrudController<Tenant> {

    private static final String BASE_PATH = "/pms/api/tenants";
    private final ITenantService tenantService;

    @Override
    protected ITenantService service() {
        return tenantService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "tenants";
    }

    @Override
    protected String entityLabel() {
        return "Tenant";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<Tenant>>> list(
            @RequestParam(value = "propertyId", required = false) UUID propertyId,
            @RequestParam(value = "unitId", required = false) UUID unitId,
            @RequestParam(value = "leaseId", required = false) UUID leaseId,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> traceParams = new LinkedHashMap<>();
        traceParams.put("propertyId", propertyId);
        traceParams.put("unitId", unitId);
        traceParams.put("leaseId", leaseId);
        return executeList(headers, traceParams,
                ctx -> tenantService.findAll(ctx, propertyId, unitId, leaseId));
    }
}
