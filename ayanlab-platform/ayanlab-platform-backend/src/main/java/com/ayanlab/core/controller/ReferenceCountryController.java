package com.ayanlab.core.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.condition.CoreBusinessApi;
import com.ayanlab.core.dto.ClientHeadersPayload;
import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.core.entity.ReferenceCountry;
import com.ayanlab.core.service.IReferenceCountryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@CoreBusinessApi
@RestController
@RequestMapping("/core/api/reference-countries")
@Tag(name = "Core · ReferenceCountry", description = "ISO 3166 country reference data")
@RequiredArgsConstructor
public class ReferenceCountryController extends BaseController {

    private static final String BASE_PATH = "/core/api/reference-countries";

    private final IReferenceCountryService referenceCountryService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<ReferenceCountry>>> list(
            @RequestParam(name = "q", required = false) String query,
            @RequestParam(name = "region", required = false) String region,
            @ClientHeaders ClientHeadersPayload headers) {
        var call = tracedCall(log, "list-reference-countries", "GET", BASE_PATH, headers, null, Map.of());
        List<ReferenceCountry> rows;
        if (region != null && !region.isBlank()) {
            rows = referenceCountryService.listByRegion(region);
        } else if (query != null && !query.isBlank()) {
            rows = referenceCountryService.search(query);
        } else {
            rows = referenceCountryService.listAll();
        }
        call.trace().success(Map.of("count", rows.size()));
        return ok(rows, call.context());
    }

    @GetMapping(value = "{alpha2}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ReferenceCountry>> getByAlpha2(
            @PathVariable String alpha2,
            @ClientHeaders ClientHeadersPayload headers) {
        var call = tracedCall(log, "get-reference-country", "GET", BASE_PATH + "/{alpha2}", headers, null,
            Map.of("alpha2", alpha2));
        return ok(referenceCountryService.findByAlpha2(alpha2), call.context());
    }
}