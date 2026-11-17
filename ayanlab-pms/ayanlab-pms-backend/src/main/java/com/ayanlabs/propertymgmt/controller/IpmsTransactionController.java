package com.ayanlabs.propertymgmt.controller;

import com.ayanlabs.propertymgmt.entity.accounting.IpmsTransaction;
import com.ayanlabs.propertymgmt.service.IIpmsTransactionService;
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
@RequestMapping("/pms/api/transactions")
@Tag(name = "IPMS · IpmsTransactionController", description = "IpmsTransaction management (Buildium-style CRUD)")
@RequiredArgsConstructor
public class IpmsTransactionController extends IpmsCrudController<IpmsTransaction> {

    private static final String BASE_PATH = "/pms/api/transactions";
    private final IIpmsTransactionService ipmsTransactionService;

    @Override
    protected IIpmsTransactionService service() {
        return ipmsTransactionService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "transactions";
    }

    @Override
    protected String entityLabel() {
        return "IpmsTransaction";
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<IpmsTransaction>>> list(
            @RequestParam(value = "propertyId", required = false) UUID propertyId,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> traceParams = new LinkedHashMap<>();
        traceParams.put("propertyId", propertyId);
        return executeList(headers, traceParams, ctx -> ((IIpmsTransactionService) service()).findAll(ctx, propertyId));
    }

}
