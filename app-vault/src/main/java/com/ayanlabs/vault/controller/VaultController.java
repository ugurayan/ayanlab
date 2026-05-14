package com.ayanlabs.vault.controller;

import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.core.provider.AppSecurityContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/ayanlabs/api/vault")
public class VaultController {

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProfile() {
        var ctx = AppSecurityContext.get();
        return ResponseEntity.ok(ApiResponse.ok(Map.of(
            "userId", ctx.getUserId(),
            "appKey", ctx.getAppKey(),
            "roles", ctx.getRoles()
        )));
    }
}
