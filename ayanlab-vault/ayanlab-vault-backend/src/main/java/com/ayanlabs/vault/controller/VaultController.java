package com.ayanlabs.vault.controller;

import com.ayanlabs.core.data.ClientHeadersPayload;

import com.ayanlabs.core.annotation.ClientHeaders;

import com.ayanlabs.core.controller.BaseController;
import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.core.provider.AppSecurityContext;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/vault/api")
@Tag(name = "Vault · VaultController", description = "Authenticated profile / me (Vault)")
public class VaultController extends BaseController {

    private static final String BASE_PATH = "/vault/api";

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProfile(
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "get-vault-profile", "GET", BASE_PATH + "/me", headers, null, null);
        try {
            var ctx = AppSecurityContext.get();
            Map<String, Object> data = Map.of(
                    "userId", ctx.getUserId(),
                    "appKey", ctx.getAppKey(),
                    "roles", ctx.getRoles());
            ResponseEntity<ApiResponse<Map<String, Object>>> response = okBody(data, call.context());
            call.trace().success(Map.of("success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }
}
