package com.ayanlab.pms.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.data.ClientHeadersPayload;
import com.ayanlab.core.data.responses.ApiResponse;
import com.ayanlab.pms.backend.entity.accounting.RecurringTransaction;
import com.ayanlab.pms.backend.service.IRecurringTransactionService;
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
@RequestMapping("/pms/api/recurring-transactions")
@Tag(name = "IPMS · RecurringTransactionController", description = "RecurringTransaction CRUD")
@RequiredArgsConstructor
public class RecurringTransactionController extends IpmsCrudController<RecurringTransaction> {

    private static final String BASE_PATH = "/pms/api/recurring-transactions";
    private final IRecurringTransactionService recurringTransactionService;

    @Override
    protected IRecurringTransactionService service() {
        return recurringTransactionService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "recurring-transactions";
    }

    @Override
    protected String entityLabel() {
        return "RecurringTransaction";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<RecurringTransaction>>> list(
            @ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }
}
