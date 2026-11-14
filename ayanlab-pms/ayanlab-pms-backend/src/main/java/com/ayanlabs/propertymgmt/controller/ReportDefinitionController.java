package com.ayanlabs.propertymgmt.controller;

import com.ayanlabs.core.annotation.ClientHeaders;
import com.ayanlabs.core.data.ClientHeadersPayload;
import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.propertymgmt.entity.reporting.ReportDefinition;
import com.ayanlabs.propertymgmt.service.IReportDefinitionService;
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
@RequestMapping("/ayanlabs/api/property-mgmt/reports")
@Tag(name = "IPMS · ReportDefinitionController", description = "ReportDefinition CRUD")
@RequiredArgsConstructor
public class ReportDefinitionController extends IpmsCrudController<ReportDefinition> {

    private static final String BASE_PATH = "/ayanlabs/api/property-mgmt/reports";
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
