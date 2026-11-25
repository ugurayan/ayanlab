package com.ayanlab.pms.backend.controller;

import com.ayanlab.pms.backend.entity.leasing.LeaseRenewal;
import com.ayanlab.pms.backend.service.ILeaseRenewalService;
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
@RequestMapping("/pms/api/lease-renewals")
@Tag(name = "IPMS · LeaseRenewalController", description = "LeaseRenewal management (Buildium-style CRUD)")
@RequiredArgsConstructor
public class LeaseRenewalController extends IpmsCrudController<LeaseRenewal> {

    private static final String BASE_PATH = "/pms/api/lease-renewals";
    private final ILeaseRenewalService leaseRenewalService;

    @Override
    protected ILeaseRenewalService service() {
        return leaseRenewalService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "lease-renewals";
    }

    @Override
    protected String entityLabel() {
        return "LeaseRenewal";
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<LeaseRenewal>>> list(
            @RequestParam(value = "leaseId", required = false) UUID leaseId,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> traceParams = new LinkedHashMap<>();
        traceParams.put("leaseId", leaseId);
        return executeList(headers, traceParams, ctx -> ((ILeaseRenewalService) service()).findAll(ctx, leaseId));
    }

}
