package com.ayanlab.pms.backend.controller;

import com.ayanlab.pms.backend.entity.tasks.IncomingRequest;
import com.ayanlab.pms.backend.service.IIncomingRequestService;

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
@RequestMapping("/pms/api/incoming-requests")
@Tag(name = "IPMS · IncomingRequestController", description = "IncomingRequest management (Buildium-style CRUD)")
@RequiredArgsConstructor
public class IncomingRequestController extends IpmsCrudController<IncomingRequest> {

    private static final String BASE_PATH = "/pms/api/incoming-requests";
    private final IIncomingRequestService incomingRequestService;

    @Override
    protected IIncomingRequestService service() {
        return incomingRequestService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "incoming-requests";
    }

    @Override
    protected String entityLabel() {
        return "IncomingRequest";
    }
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<IncomingRequest>>> list(
            @ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }

}
