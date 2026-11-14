package com.ayanlabs.auth.controller;

import com.ayanlabs.core.annotation.ClientHeaders;
import com.ayanlabs.core.controller.BaseController;
import com.ayanlabs.core.data.ClientHeadersPayload;
import com.ayanlabs.core.data.requests.*;
import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.core.data.responses.TokenResponse;
import com.ayanlabs.security.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/ayanlabs/api/auth")
@Tag(name = "Auth", description = "Register, login, token refresh (central auth service)")
@RequiredArgsConstructor
public class AuthController extends BaseController {

    private static final String BASE_PATH = "/ayanlabs/api/auth";

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<TokenResponse>> register(
            @ClientHeaders(requireTokens = false) ClientHeadersPayload headers,
            @Valid @RequestBody RegisterRequest request) {
        TracedCall call = tracedCall(log, "register", "POST", BASE_PATH + "/register", headers, request, null);
        try {
            TokenResponse data = authService.register(request, call.context());
            ResponseEntity<ApiResponse<TokenResponse>> response = createdBody(data, "User registered", call.context());
            call.trace().success(Map.of("success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(
            @ClientHeaders(requireTokens = false) ClientHeadersPayload headers,
            @Valid @RequestBody LoginRequest request) {
        TracedCall call = tracedCall(log, "login", "POST", BASE_PATH + "/login", headers, request, null);
        try {
            TokenResponse data = authService.login(request, call.context());
            ResponseEntity<ApiResponse<TokenResponse>> response = okBody(data, call.context());
            call.trace().success(Map.of("success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping("/confirm-email")
    public ResponseEntity<ApiResponse<Boolean>> confirmEmail(
            @ClientHeaders(requireTokens = false) ClientHeadersPayload headers,
            @Valid @RequestBody ConfirmCodeRequest request) {
        TracedCall call = tracedCall(log, "confirm-email", "POST", BASE_PATH + "/confirm-email", headers, request, null);
        try {
            boolean data = authService.confirmEmail(request.userId(), request.code(), call.context());
            ResponseEntity<ApiResponse<Boolean>> response = okBody(data, call.context());
            call.trace().success(Map.of("success", data));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping("/confirm-sms")
    public ResponseEntity<ApiResponse<Boolean>> confirmSms(
            @ClientHeaders(requireTokens = false) ClientHeadersPayload headers,
            @Valid @RequestBody ConfirmCodeRequest request) {
        TracedCall call = tracedCall(log, "confirm-sms", "POST", BASE_PATH + "/confirm-sms", headers, request, null);
        try {
            boolean data = authService.confirmSms(request.userId(), request.code(), call.context());
            ResponseEntity<ApiResponse<Boolean>> response = okBody(data, call.context());
            call.trace().success(Map.of("success", data));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @ClientHeaders(requireTokens = false) ClientHeadersPayload headers,
            @Valid @RequestBody ForgotRequest request) {
        TracedCall call = tracedCall(log, "forgot-password", "POST", BASE_PATH + "/forgot-password", headers, request, null);
        try {
            String data = authService.forgotPassword(request.emailOrMobile(), call.context());
            ResponseEntity<ApiResponse<String>> response = okBody(data, call.context());
            call.trace().success(Map.of("success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Boolean>> resetPassword(
            @ClientHeaders(requireTokens = false) ClientHeadersPayload headers,
            @Valid @RequestBody ResetPasswordRequest request) {
        TracedCall call = tracedCall(log, "reset-password", "POST", BASE_PATH + "/reset-password", headers, request, null);
        try {
            boolean data = authService.resetPassword(request.token(), request.newPassword(), call.context());
            ResponseEntity<ApiResponse<Boolean>> response = okBody(data, call.context());
            call.trace().success(Map.of("success", data));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping("/forgot-username")
    public ResponseEntity<ApiResponse<Boolean>> forgotUsername(
            @ClientHeaders(requireTokens = false) ClientHeadersPayload headers,
            @Valid @RequestBody ForgotRequest request) {
        TracedCall call = tracedCall(log, "forgot-username", "POST", BASE_PATH + "/forgot-username", headers, request, null);
        try {
            boolean data = authService.forgotUsername(request.emailOrMobile(), call.context());
            ResponseEntity<ApiResponse<Boolean>> response = okBody(data, call.context());
            call.trace().success(Map.of("success", data));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Boolean>> changePassword(
            @ClientHeaders(requireTokens = false) ClientHeadersPayload headers,
            @Valid @RequestBody ChangePasswordRequest request) {
        TracedCall call = tracedCall(log, "change-password", "POST", BASE_PATH + "/change-password", headers, request, null);
        try {
            boolean data = authService.changePassword(request.userId(), request.oldPassword(), request.newPassword(), call.context());
            ResponseEntity<ApiResponse<Boolean>> response = okBody(data, call.context());
            call.trace().success(Map.of("success", data));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping("/enroll")
    public ResponseEntity<ApiResponse<Object>> enrollToApp(
            @ClientHeaders(requireTokens = false) ClientHeadersPayload headers,
            @Valid @RequestBody EnrollRequest request) {
        TracedCall call = tracedCall(log, "enroll", "POST", BASE_PATH + "/enroll", headers, request, null);
        try {
            Object data = authService.enrollToApp(request, call.context());
            ResponseEntity<ApiResponse<Object>> response = okBody(data, call.context());
            call.trace().success(Map.of("success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(
            @ClientHeaders(requireTokens = false) ClientHeadersPayload headers,
            @Valid @RequestBody RefreshTokenRequest request) {
        TracedCall call = tracedCall(log, "refresh-token", "POST", BASE_PATH + "/refresh", headers, request, null);
        try {
            TokenResponse data = authService.refreshToken(request, call.context());
            ResponseEntity<ApiResponse<TokenResponse>> response = okBody(data, call.context());
            call.trace().success(Map.of("success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader("Authorization") String authHeader,
            @ClientHeaders(requireTokens = false) ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "logout", "POST", BASE_PATH + "/logout", headers, null, null);
        try {
            authService.logout(authHeader.substring(7), call.context());
            ResponseEntity<ApiResponse<Void>> response = okBody(null, "Logged out", call.context());
            call.trace().success(Map.of("success", true));
            return response;
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }
}