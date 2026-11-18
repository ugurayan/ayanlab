package com.ayanlab.pms.backend.controller;

import com.ayanlab.pms.backend.entity.communication.Message;
import com.ayanlab.pms.backend.service.IMessageService;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.data.ClientHeadersPayload;
import com.ayanlab.core.data.responses.ApiResponse;
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
@RequestMapping("/pms/api/messages")
@Tag(name = "IPMS · MessageController", description = "Message management (Buildium-style CRUD)")
@RequiredArgsConstructor
public class MessageController extends IpmsCrudController<Message> {

    private static final String BASE_PATH = "/pms/api/messages";
    private final IMessageService messageService;

    @Override
    protected IMessageService service() {
        return messageService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "messages";
    }

    @Override
    protected String entityLabel() {
        return "Message";
    }
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<Message>>> list(
            @ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }

}
