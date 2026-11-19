package com.ayanlab.pms.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.data.ClientHeadersPayload;
import com.ayanlab.core.data.responses.ApiResponse;
import com.ayanlab.pms.backend.entity.tr.EnforcementCase;
import com.ayanlab.pms.backend.service.IEnforcementCaseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pms/api/enforcement-cases")
@Tag(name = "IPMS · EnforcementCaseController", description = "Enforcement case (TR module CRUD)")
@RequiredArgsConstructor
public class EnforcementCaseController extends IpmsCrudController<EnforcementCase> {

    private static final String BASE_PATH = "/pms/api/enforcement-cases";
    private final IEnforcementCaseService enforcementCaseService;

    @Override
    protected IEnforcementCaseService service() {
        return enforcementCaseService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "enforcement-cases";
    }

    @Override
    protected String entityLabel() {
        return "EnforcementCase";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<EnforcementCase>>> list(@ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }
}
