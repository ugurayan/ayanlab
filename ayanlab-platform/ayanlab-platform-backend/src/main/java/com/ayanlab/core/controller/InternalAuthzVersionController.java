package com.ayanlab.core.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.authz.InternalTokenGuard;
import com.ayanlab.core.authz.LocalAuthzVersionChecker;
import com.ayanlab.core.constant.InternalApiHeaders;
import com.ayanlab.core.dto.ClientHeadersPayload;
import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.core.exception.CoreException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/** Service-to-service endpoint: app backends check whether an access token's authz snapshot is current. */
@Slf4j
@RestController
@RequestMapping(InternalApiHeaders.AUTHZ_VERSION_PATH)
@Tag(name = "Core · InternalAuthzVersionController", description = "Current authorization version (service token)")
@RequiredArgsConstructor
public class InternalAuthzVersionController extends BaseController {

    public record AuthzVersionView(String appCode, UUID profileId, String version) {}

    private final LocalAuthzVersionChecker versionChecker;
    private final InternalTokenGuard internalTokenGuard;

    @GetMapping
    public ResponseEntity<ApiResponse<AuthzVersionView>> currentVersion(
            @RequestParam String appCode,
            @RequestParam UUID profileId,
            @RequestHeader(value = InternalApiHeaders.INTERNAL_TOKEN, required = false) String token,
            @ClientHeaders(requireTokens = false, requireAppId = false) ClientHeadersPayload headers) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("appCode", appCode);
        params.put("profileId", profileId);
        TracedCall call = tracedCall(log, "current-authz-version", "GET", InternalApiHeaders.AUTHZ_VERSION_PATH, headers, null, params);
        try {
            internalTokenGuard.require(token);
            String version = versionChecker.currentVersion(appCode, profileId);
            if (version == null) {
                throw new CoreException("NOT_FOUND", "Unknown app/profile");
            }
            ResponseEntity<ApiResponse<AuthzVersionView>> response = okBody(new AuthzVersionView(appCode, profileId, version), call.context());
            call.trace().success(Map.of("success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }
}
