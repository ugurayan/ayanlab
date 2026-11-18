package com.ayanlab.pms.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.data.ClientHeadersPayload;
import com.ayanlab.core.data.responses.ApiResponse;
import com.ayanlab.pms.backend.entity.accounting.TaxFiling1099;
import com.ayanlab.pms.backend.service.ITaxFiling1099Service;
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
@RequestMapping("/pms/api/tax-filings-1099")
@Tag(name = "IPMS · TaxFiling1099Controller", description = "TaxFiling1099 CRUD")
@RequiredArgsConstructor
public class TaxFiling1099Controller extends IpmsCrudController<TaxFiling1099> {

    private static final String BASE_PATH = "/pms/api/tax-filings-1099";
    private final ITaxFiling1099Service taxFiling1099Service;

    @Override
    protected ITaxFiling1099Service service() {
        return taxFiling1099Service;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "tax-filings-1099";
    }

    @Override
    protected String entityLabel() {
        return "TaxFiling1099";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<TaxFiling1099>>> list(
            @ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }
}
