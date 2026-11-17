package com.ayanlabs.propertymgmt.controller;

import com.ayanlabs.propertymgmt.entity.rentals.RentalOwner;
import com.ayanlabs.propertymgmt.service.IRentalOwnerService;

import com.ayanlabs.core.annotation.ClientHeaders;
import com.ayanlabs.core.data.ClientHeadersPayload;
import com.ayanlabs.core.data.responses.ApiResponse;
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
@RequestMapping("/pms/api/rental-owners")
@Tag(name = "IPMS · RentalOwnerController", description = "RentalOwner management (Buildium-style CRUD)")
@RequiredArgsConstructor
public class RentalOwnerController extends IpmsCrudController<RentalOwner> {

    private static final String BASE_PATH = "/pms/api/rental-owners";
    private final IRentalOwnerService rentalOwnerService;

    @Override
    protected IRentalOwnerService service() {
        return rentalOwnerService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "rental-owners";
    }

    @Override
    protected String entityLabel() {
        return "RentalOwner";
    }
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<RentalOwner>>> list(
            @ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }

}
