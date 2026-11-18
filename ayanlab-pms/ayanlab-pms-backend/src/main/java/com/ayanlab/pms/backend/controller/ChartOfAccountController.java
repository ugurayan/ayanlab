package com.ayanlab.pms.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.data.ClientHeadersPayload;
import com.ayanlab.core.data.responses.ApiResponse;
import com.ayanlab.pms.backend.entity.accounting.ChartOfAccount;
import com.ayanlab.pms.backend.service.IChartOfAccountService;
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
@RequestMapping("/pms/api/chart-of-accounts")
@Tag(name = "IPMS · ChartOfAccountController", description = "ChartOfAccount CRUD")
@RequiredArgsConstructor
public class ChartOfAccountController extends IpmsCrudController<ChartOfAccount> {

    private static final String BASE_PATH = "/pms/api/chart-of-accounts";
    private final IChartOfAccountService chartOfAccountService;

    @Override
    protected IChartOfAccountService service() {
        return chartOfAccountService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "chart-of-accounts";
    }

    @Override
    protected String entityLabel() {
        return "ChartOfAccount";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<ChartOfAccount>>> list(
            @ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }
}
