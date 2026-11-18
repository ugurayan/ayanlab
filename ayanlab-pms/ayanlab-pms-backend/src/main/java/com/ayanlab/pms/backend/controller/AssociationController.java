package com.ayanlab.pms.backend.controller;

import com.ayanlab.pms.backend.entity.associations.Association;
import com.ayanlab.pms.backend.service.IAssociationService;

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
@RequestMapping("/pms/api/associations")
@Tag(name = "IPMS · AssociationController", description = "Association management (Buildium-style CRUD)")
@RequiredArgsConstructor
public class AssociationController extends IpmsCrudController<Association> {

    private static final String BASE_PATH = "/pms/api/associations";
    private final IAssociationService associationService;

    @Override
    protected IAssociationService service() {
        return associationService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "associations";
    }

    @Override
    protected String entityLabel() {
        return "Association";
    }
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<Association>>> list(
            @ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }

}
