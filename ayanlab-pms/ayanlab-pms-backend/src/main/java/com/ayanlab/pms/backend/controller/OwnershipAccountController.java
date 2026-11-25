package com.ayanlab.pms.backend.controller;

import com.ayanlab.pms.backend.entity.associations.OwnershipAccount;
import com.ayanlab.pms.backend.service.IOwnershipAccountService;
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
@RequestMapping("/pms/api/ownership-accounts")
@Tag(name = "IPMS · OwnershipAccountController", description = "OwnershipAccount management (Buildium-style CRUD)")
@RequiredArgsConstructor
public class OwnershipAccountController extends IpmsCrudController<OwnershipAccount> {

    private static final String BASE_PATH = "/pms/api/ownership-accounts";
    private final IOwnershipAccountService ownershipAccountService;

    @Override
    protected IOwnershipAccountService service() {
        return ownershipAccountService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "ownership-accounts";
    }

    @Override
    protected String entityLabel() {
        return "OwnershipAccount";
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<OwnershipAccount>>> list(
            @RequestParam(value = "associationId", required = false) UUID associationId,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> traceParams = new LinkedHashMap<>();
        traceParams.put("associationId", associationId);
        return executeList(headers, traceParams, ctx -> ((IOwnershipAccountService) service()).findAll(ctx, associationId));
    }

}
