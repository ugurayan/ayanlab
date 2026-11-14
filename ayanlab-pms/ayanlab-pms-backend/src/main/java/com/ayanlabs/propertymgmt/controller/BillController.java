package com.ayanlabs.propertymgmt.controller;

import com.ayanlabs.propertymgmt.entity.accounting.Bill;
import com.ayanlabs.propertymgmt.service.IBillService;
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
@RequestMapping("/ayanlabs/api/property-mgmt/bills")
@Tag(name = "IPMS · BillController", description = "Bill management (Buildium-style CRUD)")
@RequiredArgsConstructor
public class BillController extends IpmsCrudController<Bill> {

    private static final String BASE_PATH = "/ayanlabs/api/property-mgmt/bills";
    private final IBillService billService;

    @Override
    protected IBillService service() {
        return billService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "bills";
    }

    @Override
    protected String entityLabel() {
        return "Bill";
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<Bill>>> list(
            @RequestParam(value = "propertyId", required = false) UUID propertyId,
            @RequestParam(value = "vendorId", required = false) UUID vendorId,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> traceParams = new LinkedHashMap<>();
        traceParams.put("propertyId", propertyId);
        traceParams.put("vendorId", vendorId);
        return executeList(headers, traceParams, ctx -> ((IBillService) service()).findAll(ctx, propertyId, vendorId));
    }

}
