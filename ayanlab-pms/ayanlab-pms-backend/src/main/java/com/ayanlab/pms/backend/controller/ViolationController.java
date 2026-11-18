package com.ayanlab.pms.backend.controller;

import com.ayanlab.pms.backend.entity.associations.Violation;
import com.ayanlab.pms.backend.service.IViolationService;
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
@RequestMapping("/pms/api/violations")
@Tag(name = "IPMS · ViolationController", description = "Violation management (Buildium-style CRUD)")
@RequiredArgsConstructor
public class ViolationController extends IpmsCrudController<Violation> {

    private static final String BASE_PATH = "/pms/api/violations";
    private final IViolationService violationService;

    @Override
    protected IViolationService service() {
        return violationService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "violations";
    }

    @Override
    protected String entityLabel() {
        return "Violation";
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<Violation>>> list(
            @RequestParam(value = "associationId", required = false) UUID associationId,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> traceParams = new LinkedHashMap<>();
        traceParams.put("associationId", associationId);
        return executeList(headers, traceParams, ctx -> ((IViolationService) service()).findAll(ctx, associationId));
    }

}
