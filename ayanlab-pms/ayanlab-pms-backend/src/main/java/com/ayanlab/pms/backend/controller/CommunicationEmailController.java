package com.ayanlab.pms.backend.controller;

import com.ayanlab.pms.backend.entity.communication.CommunicationEmail;
import com.ayanlab.pms.backend.service.ICommunicationEmailService;

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
@RequestMapping("/pms/api/communication-emails")
@Tag(name = "IPMS · CommunicationEmailController", description = "CommunicationEmail management (Buildium-style CRUD)")
@RequiredArgsConstructor
public class CommunicationEmailController extends IpmsCrudController<CommunicationEmail> {

    private static final String BASE_PATH = "/pms/api/communication-emails";
    private final ICommunicationEmailService communicationEmailService;

    @Override
    protected ICommunicationEmailService service() {
        return communicationEmailService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "communication-emails";
    }

    @Override
    protected String entityLabel() {
        return "CommunicationEmail";
    }
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<CommunicationEmail>>> list(
            @ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }

}
