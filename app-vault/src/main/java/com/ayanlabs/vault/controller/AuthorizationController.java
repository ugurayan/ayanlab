package com.ayanlabs.vault.controller;

import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.core.provider.AuthorizationSnapshots;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController("vaultAuthorizationController")
@RequestMapping("/ayanlabs/api/vault/authorization")
public class AuthorizationController {

    @GetMapping("/context")
    public ResponseEntity<ApiResponse<Map<String, Object>>> context() {
        return ResponseEntity.ok(ApiResponse.ok(AuthorizationSnapshots.currentPrincipal()));
    }
}
