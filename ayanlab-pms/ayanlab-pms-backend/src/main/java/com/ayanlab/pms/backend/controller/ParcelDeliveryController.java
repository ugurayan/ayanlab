package com.ayanlab.pms.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.dto.ClientHeadersPayload;
import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.pms.backend.entity.tr.ParcelDelivery;
import com.ayanlab.pms.backend.service.IParcelDeliveryService;
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
@RequestMapping("/pms/api/parcel-deliveries")
@Tag(name = "IPMS · ParcelDeliveryController", description = "Parcel delivery (TR module CRUD)")
@RequiredArgsConstructor
public class ParcelDeliveryController extends IpmsCrudController<ParcelDelivery> {

    private static final String BASE_PATH = "/pms/api/parcel-deliveries";
    private final IParcelDeliveryService parcelDeliveryService;

    @Override
    protected IParcelDeliveryService service() {
        return parcelDeliveryService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "parcel-deliveries";
    }

    @Override
    protected String entityLabel() {
        return "ParcelDelivery";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<ParcelDelivery>>> list(@ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }
}
