package com.ayanlab.pms.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.dto.ClientHeadersPayload;
import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.pms.backend.entity.settings.SubscriptionAccount;
import com.ayanlab.pms.backend.service.ISubscriptionAccountService;
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
@RequestMapping("/pms/api/subscription-accounts")
@Tag(name = "IPMS · SubscriptionAccountController", description = "SubscriptionAccount CRUD")
@RequiredArgsConstructor
public class SubscriptionAccountController extends IpmsCrudController<SubscriptionAccount> {

    private static final String BASE_PATH = "/pms/api/subscription-accounts";
    private final ISubscriptionAccountService subscriptionAccountService;

    @Override
    protected ISubscriptionAccountService service() {
        return subscriptionAccountService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "subscription-accounts";
    }

    @Override
    protected String entityLabel() {
        return "SubscriptionAccount";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<SubscriptionAccount>>> list(
            @ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }
}
