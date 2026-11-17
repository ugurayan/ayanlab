package com.ayanlabs.core.controller;

import com.ayanlabs.core.condition.AuthPlatformApi;
import com.ayanlabs.core.web.ApiPathPrefixes;
import com.ayanlabs.core.annotation.ClientHeaders;
import com.ayanlabs.core.data.ClientHeadersPayload;
import com.ayanlabs.core.data.dto.EffectiveAuthorization;
import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.core.service.IAuthorizationResolverService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@AuthPlatformApi
@RestController
@RequestMapping(ApiPathPrefixes.AUTH + "/authorization")
@Tag(name = "Auth · ProfileAuthorizationController", description = "Effective profile authorization resolution")
@RequiredArgsConstructor
public class ProfileAuthorizationController extends BaseController {

    private static final String BASE_PATH = ApiPathPrefixes.AUTH + "/authorization";

    private final IAuthorizationResolverService authorizationResolverService;

    @GetMapping(value = "/effective", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<EffectiveAuthorization>> effectiveForCurrentProfile(
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "authorization-effective", "GET", BASE_PATH + "/effective", headers, null, null);
        try {
            EffectiveAuthorization data = authorizationResolverService.resolveForCurrentProfile(call.context());
            ResponseEntity<ApiResponse<EffectiveAuthorization>> response = ok(data, call.context());
            call.trace().success(Map.of("permissionCount", data.getPermissionCodes().size(), "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/profiles/{profileId}/effective", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<EffectiveAuthorization>> effectiveForProfile(
            @PathVariable UUID profileId,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("profileId", profileId);
        TracedCall call = tracedCall(
            log, "authorization-effective-profile", "GET", BASE_PATH + "/profiles/{profileId}/effective",
            headers, null, params);
        try {
            EffectiveAuthorization data = authorizationResolverService.resolveForProfile(profileId, call.context());
            ResponseEntity<ApiResponse<EffectiveAuthorization>> response = ok(data, call.context());
            call.trace().success(Map.of("permissionCount", data.getPermissionCodes().size(), "success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }
}
