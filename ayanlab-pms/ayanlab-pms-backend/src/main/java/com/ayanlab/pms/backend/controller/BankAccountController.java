package com.ayanlab.pms.backend.controller;

import com.ayanlab.pms.backend.entity.accounting.BankAccount;
import com.ayanlab.pms.backend.service.IBankAccountService;

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
@RequestMapping("/pms/api/bank-accounts")
@Tag(name = "IPMS · BankAccountController", description = "BankAccount management (Buildium-style CRUD)")
@RequiredArgsConstructor
public class BankAccountController extends IpmsCrudController<BankAccount> {

    private static final String BASE_PATH = "/pms/api/bank-accounts";
    private final IBankAccountService bankAccountService;

    @Override
    protected IBankAccountService service() {
        return bankAccountService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "bank-accounts";
    }

    @Override
    protected String entityLabel() {
        return "BankAccount";
    }
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<BankAccount>>> list(
            @ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }

}
