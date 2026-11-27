package com.ayanlab.core.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.condition.CoreBusinessApi;
import com.ayanlab.core.dto.ClientHeadersPayload;
import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.core.entity.BankRoutingInstitution;
import com.ayanlab.core.service.IBankRoutingInstitutionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@CoreBusinessApi
@RestController
@RequestMapping("/core/api/bank-routing-institutions")
@Tag(name = "Core · BankRoutingInstitution", description = "ABA routing number directory (reference data)")
@RequiredArgsConstructor
public class BankRoutingInstitutionController extends BaseController {

    private static final String BASE_PATH = "/core/api/bank-routing-institutions";

    private final IBankRoutingInstitutionService bankRoutingInstitutionService;

    @GetMapping(value = "lookup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<BankRoutingInstitution>> lookup(
            @RequestParam(name = "routing") String routing,
            @ClientHeaders ClientHeadersPayload headers) {
        var call = tracedCall(log, "lookup-bank-routing", "GET", BASE_PATH + "/lookup", headers, null,
            Map.of("routing", routing));
        BankRoutingInstitution row = bankRoutingInstitutionService.lookupByRoutingNumber(routing);
        call.trace().success(new LinkedHashMap<>(Map.of("found", row != null)));
        return ok(row, call.context());
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<BankRoutingInstitution>>> search(
            @RequestParam(name = "q") String query,
            @ClientHeaders ClientHeadersPayload headers) {
        var call = tracedCall(log, "search-bank-routing", "GET", BASE_PATH, headers, null, Map.of("q", query));
        List<BankRoutingInstitution> rows = bankRoutingInstitutionService.search(query);
        call.trace().success(Map.of("count", rows.size()));
        return ok(rows, call.context());
    }
}