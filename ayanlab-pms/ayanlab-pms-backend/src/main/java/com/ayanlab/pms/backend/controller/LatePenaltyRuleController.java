package com.ayanlab.pms.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.data.ClientHeadersPayload;
import com.ayanlab.core.data.responses.ApiResponse;
import com.ayanlab.pms.backend.entity.tr.LatePenaltyRule;
import com.ayanlab.pms.backend.service.ILatePenaltyRuleService;
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
@RequestMapping("/pms/api/late-penalty-rules")
@Tag(name = "IPMS · LatePenaltyRuleController", description = "Late penalty rule (TR module CRUD)")
@RequiredArgsConstructor
public class LatePenaltyRuleController extends IpmsCrudController<LatePenaltyRule> {

    private static final String BASE_PATH = "/pms/api/late-penalty-rules";
    private final ILatePenaltyRuleService latePenaltyRuleService;

    @Override
    protected ILatePenaltyRuleService service() {
        return latePenaltyRuleService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "late-penalty-rules";
    }

    @Override
    protected String entityLabel() {
        return "LatePenaltyRule";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<LatePenaltyRule>>> list(@ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }
}
