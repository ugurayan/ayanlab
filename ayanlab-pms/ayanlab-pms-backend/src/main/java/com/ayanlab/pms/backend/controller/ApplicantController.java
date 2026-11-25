package com.ayanlab.pms.backend.controller;

import com.ayanlab.pms.backend.entity.leasing.Applicant;
import com.ayanlab.pms.backend.service.IApplicantService;
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
@RequestMapping("/pms/api/applicants")
@Tag(name = "IPMS · ApplicantController", description = "Applicant management (Buildium-style CRUD)")
@RequiredArgsConstructor
public class ApplicantController extends IpmsCrudController<Applicant> {

    private static final String BASE_PATH = "/pms/api/applicants";
    private final IApplicantService applicantService;

    @Override
    protected IApplicantService service() {
        return applicantService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "applicants";
    }

    @Override
    protected String entityLabel() {
        return "Applicant";
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<Applicant>>> list(
            @RequestParam(value = "listingId", required = false) UUID listingId,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> traceParams = new LinkedHashMap<>();
        traceParams.put("listingId", listingId);
        return executeList(headers, traceParams, ctx -> ((IApplicantService) service()).findAll(ctx, listingId));
    }

}
