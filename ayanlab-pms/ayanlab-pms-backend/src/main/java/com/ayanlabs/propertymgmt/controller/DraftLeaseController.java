package com.ayanlabs.propertymgmt.controller;

import com.ayanlabs.propertymgmt.entity.leasing.DraftLease;
import com.ayanlabs.propertymgmt.service.IDraftLeaseService;
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
@RequestMapping("/pms/api/draft-leases")
@Tag(name = "IPMS · DraftLeaseController", description = "DraftLease management (Buildium-style CRUD)")
@RequiredArgsConstructor
public class DraftLeaseController extends IpmsCrudController<DraftLease> {

    private static final String BASE_PATH = "/pms/api/draft-leases";
    private final IDraftLeaseService draftLeaseService;

    @Override
    protected IDraftLeaseService service() {
        return draftLeaseService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "draft-leases";
    }

    @Override
    protected String entityLabel() {
        return "DraftLease";
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<DraftLease>>> list(
            @RequestParam(value = "propertyId", required = false) UUID propertyId,
            @RequestParam(value = "unitId", required = false) UUID unitId,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> traceParams = new LinkedHashMap<>();
        traceParams.put("propertyId", propertyId);
        traceParams.put("unitId", unitId);
        return executeList(headers, traceParams, ctx -> ((IDraftLeaseService) service()).findAll(ctx, propertyId, unitId));
    }

}
