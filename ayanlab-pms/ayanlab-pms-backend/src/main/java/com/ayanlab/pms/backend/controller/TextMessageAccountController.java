package com.ayanlab.pms.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.data.ClientHeadersPayload;
import com.ayanlab.core.data.responses.ApiResponse;
import com.ayanlab.pms.backend.entity.communication.TextMessageAccount;
import com.ayanlab.pms.backend.service.ITextMessageAccountService;
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
@RequestMapping("/pms/api/text-message-accounts")
@Tag(name = "IPMS · TextMessageAccountController", description = "TextMessageAccount CRUD")
@RequiredArgsConstructor
public class TextMessageAccountController extends IpmsCrudController<TextMessageAccount> {

    private static final String BASE_PATH = "/pms/api/text-message-accounts";
    private final ITextMessageAccountService textMessageAccountService;

    @Override
    protected ITextMessageAccountService service() {
        return textMessageAccountService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "text-message-accounts";
    }

    @Override
    protected String entityLabel() {
        return "TextMessageAccount";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<TextMessageAccount>>> list(
            @ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }
}
