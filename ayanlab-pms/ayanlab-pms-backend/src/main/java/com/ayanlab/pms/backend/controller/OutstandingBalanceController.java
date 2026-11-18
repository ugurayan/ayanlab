package com.ayanlab.pms.backend.controller;

import com.ayanlab.pms.backend.entity.rentals.OutstandingBalance;
import com.ayanlab.pms.backend.service.IOutstandingBalanceService;
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
@RequestMapping("/pms/api/outstanding-balances")
@Tag(name = "IPMS · OutstandingBalanceController", description = "OutstandingBalance management (Buildium-style CRUD)")
@RequiredArgsConstructor
public class OutstandingBalanceController extends IpmsCrudController<OutstandingBalance> {

    private static final String BASE_PATH = "/pms/api/outstanding-balances";
    private final IOutstandingBalanceService outstandingBalanceService;

    @Override
    protected IOutstandingBalanceService service() {
        return outstandingBalanceService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "outstanding-balances";
    }

    @Override
    protected String entityLabel() {
        return "OutstandingBalance";
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<OutstandingBalance>>> list(
            @RequestParam(value = "leaseId", required = false) UUID leaseId,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> traceParams = new LinkedHashMap<>();
        traceParams.put("leaseId", leaseId);
        return executeList(headers, traceParams, ctx -> ((IOutstandingBalanceService) service()).findAll(ctx, leaseId));
    }

}
