package com.ayanlabs.propertymgmt.controller;

import com.ayanlabs.core.annotation.ClientHeaders;
import com.ayanlabs.core.data.ClientHeadersPayload;
import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.propertymgmt.entity.accounting.Budget;
import com.ayanlabs.propertymgmt.service.IBudgetService;
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
@RequestMapping("/pms/api/budgets")
@Tag(name = "IPMS · BudgetController", description = "Budget CRUD")
@RequiredArgsConstructor
public class BudgetController extends IpmsCrudController<Budget> {

    private static final String BASE_PATH = "/pms/api/budgets";
    private final IBudgetService budgetService;

    @Override
    protected IBudgetService service() {
        return budgetService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "budgets";
    }

    @Override
    protected String entityLabel() {
        return "Budget";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<Budget>>> list(
            @ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }
}
