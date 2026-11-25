package com.ayanlab.pms.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.dto.ClientHeadersPayload;
import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.pms.backend.entity.tr.DebtInquiryCode;
import com.ayanlab.pms.backend.service.IDebtInquiryCodeService;
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
@RequestMapping("/pms/api/debt-inquiry-codes")
@Tag(name = "IPMS · DebtInquiryCodeController", description = "Debt inquiry code (TR module CRUD)")
@RequiredArgsConstructor
public class DebtInquiryCodeController extends IpmsCrudController<DebtInquiryCode> {

    private static final String BASE_PATH = "/pms/api/debt-inquiry-codes";
    private final IDebtInquiryCodeService debtInquiryCodeService;

    @Override
    protected IDebtInquiryCodeService service() {
        return debtInquiryCodeService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "debt-inquiry-codes";
    }

    @Override
    protected String entityLabel() {
        return "DebtInquiryCode";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<DebtInquiryCode>>> list(@ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }
}
