package com.ayanlab.core.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.authz.InternalTokenGuard;
import com.ayanlab.core.authz.catalog.AppAuthorizationCatalog;
import com.ayanlab.core.authz.catalog.InternalApiHeaders;
import com.ayanlab.core.data.ClientHeadersPayload;
import com.ayanlab.core.data.responses.ApiResponse;
import com.ayanlab.core.service.impl.AuthorizationCatalogImportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/** Service-to-service endpoint: app backends publish their authorization catalog on startup. */
@Slf4j
@RestController
@RequestMapping(InternalApiHeaders.CATALOG_PATH)
@Tag(name = "Core · InternalAuthorizationCatalogController", description = "App authorization catalog import (service token)")
@RequiredArgsConstructor
public class InternalAuthorizationCatalogController extends BaseController {

    private final AuthorizationCatalogImportService importService;
    private final InternalTokenGuard internalTokenGuard;

    @PostMapping
    public ResponseEntity<ApiResponse<AuthorizationCatalogImportService.ImportResult>> importCatalog(
            @RequestBody AppAuthorizationCatalog catalog,
            @RequestHeader(value = InternalApiHeaders.INTERNAL_TOKEN, required = false) String token,
            @ClientHeaders(requireTokens = false, requireAppId = false) ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "import-authorization-catalog", "POST", InternalApiHeaders.CATALOG_PATH, headers,
                Map.of("appCode", String.valueOf(catalog.appCode())), null);
        try {
            internalTokenGuard.require(token);
            AuthorizationCatalogImportService.ImportResult result = importService.importCatalog(catalog);
            ResponseEntity<ApiResponse<AuthorizationCatalogImportService.ImportResult>> response = okBody(result, call.context());
            call.trace().success(Map.of("success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }
}
