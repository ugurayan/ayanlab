package com.ayanlabs.propertymgmt.controller;

import com.ayanlabs.propertymgmt.entity.tasks.IncomingRequest;
import com.ayanlabs.propertymgmt.service.IIncomingRequestService;

import com.ayanlabs.core.annotation.ClientHeaders;
import com.ayanlabs.core.data.ClientHeadersPayload;
import com.ayanlabs.core.data.responses.ApiResponse;
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
@RequestMapping("/ayanlabs/api/property-mgmt/incoming-requests")
@Tag(name = "IPMS · IncomingRequestController", description = "IncomingRequest management (Buildium-style CRUD)")
@RequiredArgsConstructor
public class IncomingRequestController extends IpmsCrudController<IncomingRequest> {

    private static final String BASE_PATH = "/ayanlabs/api/property-mgmt/incoming-requests";
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
