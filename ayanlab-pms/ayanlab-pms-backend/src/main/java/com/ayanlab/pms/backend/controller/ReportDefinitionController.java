package com.ayanlab.pms.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.dto.ClientHeadersPayload;
import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.pms.backend.entity.reporting.ReportDefinition;
import com.ayanlab.pms.backend.service.IReportDefinitionService;
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
@RequestMapping("/pms/api/reports")
@Tag(name = "IPMS · ReportDefinitionController", description = "ReportDefinition CRUD")
@RequiredArgsConstructor
public class ReportDefinitionController extends IpmsCrudController<ReportDefinition> {

    private static final String BASE_PATH = "/pms/api/reports";
    private final IReportDefinitionService reportDefinitionService;

    @Override
    protected IReportDefinitionService service() {
        return reportDefinitionService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "reports";
    }

    @Override
    protected String entityLabel() {
        return "ReportDefinition";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<ReportDefinition>>> list(
            @ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }
}
