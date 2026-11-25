package com.ayanlab.core.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.condition.AuthPlatformApi;
import com.ayanlab.core.dto.ClientHeadersPayload;
import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.core.entity.LogError;
import com.ayanlab.core.service.ILogErrorService;
import com.ayanlab.core.constant.ApiPathPrefixes;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@AuthPlatformApi
@RestController
@RequestMapping(ApiPathPrefixes.AUTH + "/log-errors")
@Tag(name = "Auth · LogError", description = "Error audit (tmp/auth)")
@RequiredArgsConstructor
public class LogErrorController extends BaseController {

    private final ILogErrorService logErrorService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<LogError>>> recent(
            @RequestParam(defaultValue = "CREATED") String status,
            @ClientHeaders ClientHeadersPayload headers) {
        var call = tracedCall(log, "log-errors", "GET", ApiPathPrefixes.AUTH + "/log-errors", headers, null, null);
        return ok(logErrorService.findRecent(status, call.context()), call.context());
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<LogError>> get(@PathVariable UUID id, @ClientHeaders ClientHeadersPayload headers) {
        var call = tracedCall(log, "log-error", "GET", ApiPathPrefixes.AUTH + "/log-errors/{id}", headers, null, null);
        return ok(logErrorService.findById(id, call.context()), call.context());
    }
}