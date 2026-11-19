package com.ayanlab.pms.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.data.ClientHeadersPayload;
import com.ayanlab.core.data.responses.ApiResponse;
import com.ayanlab.pms.backend.entity.leasing.CommercialLeaseTerms;
import com.ayanlab.pms.backend.service.ICommercialLeaseTermsService;
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
@RequestMapping("/pms/api/commercial-lease-terms")
@Tag(name = "IPMS · CommercialLeaseTermsController", description = "Commercial lease terms (P5.8)")
@RequiredArgsConstructor
public class CommercialLeaseTermsController extends IpmsCrudController<CommercialLeaseTerms> {

    private static final String BASE_PATH = "/pms/api/commercial-lease-terms";
    private final ICommercialLeaseTermsService commercialLeaseTermsService;

    @Override
    protected ICommercialLeaseTermsService service() {
        return commercialLeaseTermsService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "commercial-lease-terms";
    }

    @Override
    protected String entityLabel() {
        return "CommercialLeaseTerms";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<CommercialLeaseTerms>>> list(@ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }
}
