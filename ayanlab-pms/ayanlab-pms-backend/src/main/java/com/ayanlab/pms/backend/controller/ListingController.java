package com.ayanlab.pms.backend.controller;

import com.ayanlab.pms.backend.entity.leasing.Listing;
import com.ayanlab.pms.backend.service.IListingService;
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
@RequestMapping("/pms/api/listings")
@Tag(name = "IPMS · ListingController", description = "Listing management (Buildium-style CRUD)")
@RequiredArgsConstructor
public class ListingController extends IpmsCrudController<Listing> {

    private static final String BASE_PATH = "/pms/api/listings";
    private final IListingService listingService;

    @Override
    protected IListingService service() {
        return listingService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "listings";
    }

    @Override
    protected String entityLabel() {
        return "Listing";
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<Listing>>> list(
            @RequestParam(value = "propertyId", required = false) UUID propertyId,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> traceParams = new LinkedHashMap<>();
        traceParams.put("propertyId", propertyId);
        return executeList(headers, traceParams, ctx -> ((IListingService) service()).findAll(ctx, propertyId));
    }

}
