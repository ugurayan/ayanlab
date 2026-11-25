package com.ayanlab.pms.backend.controller;

import com.ayanlab.pms.backend.entity.maintenance.Vendor;
import com.ayanlab.pms.backend.service.IVendorService;

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
@RequestMapping("/pms/api/vendors")
@Tag(name = "IPMS · VendorController", description = "Vendor management (Buildium-style CRUD)")
@RequiredArgsConstructor
public class VendorController extends IpmsCrudController<Vendor> {

    private static final String BASE_PATH = "/pms/api/vendors";
    private final IVendorService vendorService;

    @Override
    protected IVendorService service() {
        return vendorService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "vendors";
    }

    @Override
    protected String entityLabel() {
        return "Vendor";
    }
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<Vendor>>> list(
            @ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }

}
