package com.ayanlab.pms.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.data.ClientHeadersPayload;
import com.ayanlab.core.data.responses.ApiResponse;
import com.ayanlab.pms.backend.entity.leasing.RentersInsurancePolicy;
import com.ayanlab.pms.backend.service.IRentersInsurancePolicyService;
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
@RequestMapping("/pms/api/renters-insurance")
@Tag(name = "IPMS · RentersInsurancePolicyController", description = "Renters insurance policies")
@RequiredArgsConstructor
public class RentersInsurancePolicyController extends IpmsCrudController<RentersInsurancePolicy> {

    private static final String BASE_PATH = "/pms/api/renters-insurance";
    private final IRentersInsurancePolicyService rentersInsurancePolicyService;

    @Override
    protected IRentersInsurancePolicyService service() {
        return rentersInsurancePolicyService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "renters-insurance";
    }

    @Override
    protected String entityLabel() {
        return "RentersInsurancePolicy";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<RentersInsurancePolicy>>> list(
            @ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }
}
