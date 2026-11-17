package com.ayanlabs.propertymgmt.controller;

import com.ayanlabs.core.annotation.ClientHeaders;
import com.ayanlabs.core.data.ClientHeadersPayload;
import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.propertymgmt.entity.communication.TextMessage;
import com.ayanlabs.propertymgmt.service.ITextMessageService;
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
@RequestMapping("/pms/api/text-messages")
@Tag(name = "IPMS · TextMessageController", description = "TextMessage CRUD")
@RequiredArgsConstructor
public class TextMessageController extends IpmsCrudController<TextMessage> {

    private static final String BASE_PATH = "/pms/api/text-messages";
    private final ITextMessageService textMessageService;

    @Override
    protected ITextMessageService service() {
        return textMessageService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "text-messages";
    }

    @Override
    protected String entityLabel() {
        return "TextMessage";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<TextMessage>>> list(
            @ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }
}
