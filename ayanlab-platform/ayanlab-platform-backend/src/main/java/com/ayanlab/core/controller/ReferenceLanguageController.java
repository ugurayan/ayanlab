package com.ayanlab.core.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.condition.CoreBusinessApi;
import com.ayanlab.core.dto.ClientHeadersPayload;
import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.core.entity.ReferenceLanguage;
import com.ayanlab.core.service.IReferenceLanguageService;
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
@RequestMapping("/core/api/reference-languages")
@Tag(name = "Core · ReferenceLanguage", description = "ISO 639 language reference data")
@RequiredArgsConstructor
public class ReferenceLanguageController extends BaseController {

    private static final String BASE_PATH = "/core/api/reference-languages";

    private final IReferenceLanguageService referenceLanguageService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<ReferenceLanguage>>> list(
            @RequestParam(name = "q", required = false) String query,
            @ClientHeaders ClientHeadersPayload headers) {
        var call = tracedCall(log, "list-reference-languages", "GET", BASE_PATH, headers, null, Map.of());
        List<ReferenceLanguage> rows = (query != null && !query.isBlank())
            ? referenceLanguageService.search(query)
            : referenceLanguageService.listAll();
        call.trace().success(Map.of("count", rows.size()));
        return ok(rows, call.context());
    }

    @GetMapping(value = "{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ReferenceLanguage>> getByCode(
            @PathVariable String code,
            @ClientHeaders ClientHeadersPayload headers) {
        var call = tracedCall(log, "get-reference-language", "GET", BASE_PATH + "/{code}", headers, null,
            Map.of("code", code));
        return ok(referenceLanguageService.findByCode(code), call.context());
    }
}