package com.ayanlabs.propertymgmt.controller;

import com.ayanlabs.propertymgmt.entity.accounting.FinancialRecord;
import com.ayanlabs.propertymgmt.service.IFinancialRecordService;
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
@RequestMapping("/ayanlabs/api/property-mgmt/financial-records")
@Tag(name = "IPMS · FinancialRecordController", description = "FinancialRecord management (Buildium-style CRUD)")
@RequiredArgsConstructor
public class FinancialRecordController extends IpmsCrudController<FinancialRecord> {

    private static final String BASE_PATH = "/ayanlabs/api/property-mgmt/financial-records";
    private final IFinancialRecordService financialRecordService;

    @Override
    protected IFinancialRecordService service() {
        return financialRecordService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "financial-records";
    }

    @Override
    protected String entityLabel() {
        return "FinancialRecord";
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<FinancialRecord>>> list(
            @RequestParam(value = "propertyId", required = false) UUID propertyId,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> traceParams = new LinkedHashMap<>();
        traceParams.put("propertyId", propertyId);
        return executeList(headers, traceParams, ctx -> ((IFinancialRecordService) service()).findAll(ctx, propertyId));
    }

}
