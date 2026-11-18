package com.ayanlab.pms.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.data.ClientHeadersPayload;
import com.ayanlab.core.data.responses.ApiResponse;
import com.ayanlab.pms.backend.entity.associations.ArchitecturalRequest;
import com.ayanlab.pms.backend.service.IArchitecturalRequestService;
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
@RequestMapping("/pms/api/architectural-requests")
@Tag(name = "IPMS · ArchitecturalRequestController", description = "ArchitecturalRequest CRUD")
@RequiredArgsConstructor
public class ArchitecturalRequestController extends IpmsCrudController<ArchitecturalRequest> {

    private static final String BASE_PATH = "/pms/api/architectural-requests";
    private final IArchitecturalRequestService architecturalRequestService;

    @Override
    protected IArchitecturalRequestService service() {
        return architecturalRequestService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "architectural-requests";
    }

    @Override
    protected String entityLabel() {
        return "ArchitecturalRequest";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<ArchitecturalRequest>>> list(
            @ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }
}
