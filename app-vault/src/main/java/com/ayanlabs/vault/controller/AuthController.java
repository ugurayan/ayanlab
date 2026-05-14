package com.ayanlabs.vault.controller;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.requests.*;
import com.ayanlabs.core.data.responses.TokenResponse;
import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.security.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/ayanlabs/api/vault/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private CoreContext buildContext(UUID appId) {
        return CoreContext.builder().appId(appId).build();
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<TokenResponse>> register(
            @RequestHeader("X-App-Id") UUID appId,
            @RequestHeader(value = "X-Correlation-Id", required = false) UUID correlationId,
            @RequestHeader(value = "X-Client-Language", required = false) String language,
            @Valid @RequestBody RegisterRequest request) {
        CoreContext context = buildContext(appId);
        if (correlationId != null) context.setCorrelationId(correlationId);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok(authService.register(request, context), "User registered"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(
            @RequestHeader("X-App-Id") UUID appId,
            @RequestHeader(value = "X-Correlation-Id", required = false) UUID correlationId,
            @Valid @RequestBody LoginRequest request) {
        CoreContext context = buildContext(appId);
        if (correlationId != null) context.setCorrelationId(correlationId);
        return ResponseEntity.ok(ApiResponse.ok(authService.login(request, context)));
    }

    @PostMapping("/confirm-email")
    public ResponseEntity<ApiResponse<Boolean>> confirmEmail(
            @RequestHeader("X-App-Id") UUID appId,
            @RequestHeader(value = "X-Correlation-Id", required = false) UUID correlationId,
            @Valid @RequestBody ConfirmCodeRequest request) {
        CoreContext context = buildContext(appId);
        if (correlationId != null) context.setCorrelationId(correlationId);
        boolean ok = authService.confirmEmail(request.userId(), request.code(), context);
        return ResponseEntity.ok(ApiResponse.ok(ok));
    }

    @PostMapping("/confirm-sms")
    public ResponseEntity<ApiResponse<Boolean>> confirmSms(
            @RequestHeader("X-App-Id") UUID appId,
            @RequestHeader(value = "X-Correlation-Id", required = false) UUID correlationId,
            @Valid @RequestBody ConfirmCodeRequest request) {
        CoreContext context = buildContext(appId);
        if (correlationId != null) context.setCorrelationId(correlationId);
        boolean ok = authService.confirmSms(request.userId(), request.code(), context);
        return ResponseEntity.ok(ApiResponse.ok(ok));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @RequestHeader("X-App-Id") UUID appId,
            @RequestHeader(value = "X-Correlation-Id", required = false) UUID correlationId,
            @Valid @RequestBody ForgotRequest request) {
        CoreContext context = buildContext(appId);
        if (correlationId != null) context.setCorrelationId(correlationId);
        String token = authService.forgotPassword(request.emailOrMobile(), context);
        return ResponseEntity.ok(ApiResponse.ok(token));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Boolean>> resetPassword(
            @RequestHeader("X-App-Id") UUID appId,
            @RequestHeader(value = "X-Correlation-Id", required = false) UUID correlationId,
            @Valid @RequestBody ResetPasswordRequest request) {
        CoreContext context = buildContext(appId);
        if (correlationId != null) context.setCorrelationId(correlationId);
        boolean ok = authService.resetPassword(request.token(), request.newPassword(), context);
        return ResponseEntity.ok(ApiResponse.ok(ok));
    }

    @PostMapping("/forgot-username")
    public ResponseEntity<ApiResponse<Boolean>> forgotUsername(
            @RequestHeader("X-App-Id") UUID appId,
            @RequestHeader(value = "X-Correlation-Id", required = false) UUID correlationId,
            @Valid @RequestBody ForgotRequest request) {
        CoreContext context = buildContext(appId);
        if (correlationId != null) context.setCorrelationId(correlationId);
        boolean ok = authService.forgotUsername(request.emailOrMobile(), context);
        return ResponseEntity.ok(ApiResponse.ok(ok));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Boolean>> changePassword(
            @RequestHeader("X-App-Id") UUID appId,
            @RequestHeader(value = "X-Correlation-Id", required = false) UUID correlationId,
            @Valid @RequestBody ChangePasswordRequest request) {
        CoreContext context = buildContext(appId);
        if (correlationId != null) context.setCorrelationId(correlationId);
        boolean ok = authService.changePassword(request.userId(), request.oldPassword(), request.newPassword(), context);
        return ResponseEntity.ok(ApiResponse.ok(ok));
    }

    @PostMapping("/enroll")
    public ResponseEntity<ApiResponse<Object>> enrollToApp(
            @RequestHeader("X-App-Id") UUID appId,
            @Valid @RequestBody EnrollRequest request) {
        CoreContext context = buildContext(appId);
        return ResponseEntity.ok(ApiResponse.ok(authService.enrollToApp(request, context)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(
            @RequestHeader("X-App-Id") UUID appId,
            @Valid @RequestBody RefreshTokenRequest request) {
        CoreContext context = buildContext(appId);
        return ResponseEntity.ok(ApiResponse.ok(authService.refreshToken(request, context)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader("X-App-Id") UUID appId,
            @RequestHeader("Authorization") String authHeader) {
        CoreContext context = buildContext(appId);
        authService.logout(authHeader.substring(7), context);
        return ResponseEntity.ok(ApiResponse.ok(null, "Logged out"));
    }
}
