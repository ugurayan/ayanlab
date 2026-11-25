package com.ayanlab.auth.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.controller.BaseController;
import com.ayanlab.core.dto.ClientHeadersPayload;
import com.ayanlab.core.dto.request.*;
import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.core.dto.response.AppCatalogEntry;
import com.ayanlab.core.dto.response.CurrentSessionResponse;
import com.ayanlab.core.dto.response.TokenResponse;
import com.ayanlab.core.entity.User;
import com.ayanlab.core.entity.UserProfile;
import com.ayanlab.core.repository.AppRepository;
import com.ayanlab.core.service.IAuthorizationResolverService;
import com.ayanlab.core.service.IProfileService;
import com.ayanlab.core.service.IUserService;
import com.ayanlab.security.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth/api")
@Tag(name = "Auth", description = "Register, login, token refresh (central auth service)")
@RequiredArgsConstructor
public class AuthController extends BaseController {

    private static final String BASE_PATH = "/auth/api";

    private final AuthService authService;
    private final AppRepository appRepository;
    private final IUserService userService;
    private final IProfileService profileService;
    private final IAuthorizationResolverService authorizationResolverService;

    @GetMapping("/apps/catalog")
    public ResponseEntity<ApiResponse<java.util.List<AppCatalogEntry>>> appCatalog(
            @ClientHeaders(requireTokens = false, requireAppId = false) ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "app-catalog", "GET", BASE_PATH + "/apps/catalog", headers, null, null);
        try {
            java.util.List<AppCatalogEntry> entries = appRepository.findAll().stream()
                    .filter(a -> Boolean.TRUE.equals(a.getIsActive()))
                    .map(a -> new AppCatalogEntry(a.getId(), a.getCode(), a.getName()))
                    .sorted(java.util.Comparator.comparing(AppCatalogEntry::name))
                    .toList();
            return okBody(entries, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

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

    @GetMapping("/profiles")
    public ResponseEntity<ApiResponse<java.util.List<com.ayanlab.core.entity.UserProfile>>> listProfiles(
            @ClientHeaders(requireTokens = false) ClientHeadersPayload headers,
            @RequestParam java.util.UUID userId,
            @RequestParam(required = false) java.util.UUID appId) {
        java.util.UUID resolvedAppId = appId;
        if (resolvedAppId == null && headers.appId() != null && !headers.appId().isBlank()) {
            resolvedAppId = java.util.UUID.fromString(headers.appId());
        }
        TracedCall call = tracedCall(log, "list-profiles", "GET", BASE_PATH + "/profiles", headers, null,
            java.util.Map.of("userId", String.valueOf(userId), "appId", String.valueOf(resolvedAppId)));
        try {
            var data = authService.listProfilesForUser(userId, resolvedAppId);
            return okBody(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping("/enroll")
    public ResponseEntity<ApiResponse<TokenResponse>> enrollToApp(
            @ClientHeaders(requireTokens = false) ClientHeadersPayload headers,
            @Valid @RequestBody EnrollRequest request) {
        TracedCall call = tracedCall(log, "enroll", "POST", BASE_PATH + "/enroll", headers, request, null);
        try {
            TokenResponse data = authService.enrollToApp(request, call.context());
            ResponseEntity<ApiResponse<TokenResponse>> response = okBody(data, call.context());
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

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CurrentSessionResponse>> me(
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "current-session", "GET", BASE_PATH + "/me", headers, null, null);
        try {
            var ctx = call.context();
            User user = ctx.getUserId() != null ? userService.findById(ctx.getUserId(), ctx) : null;
            java.util.List<UserProfile> profiles = ctx.getUserId() != null
                    ? profileService.findByUser(ctx.getUserId(), ctx)
                    : java.util.List.of();
            CurrentSessionResponse data = CurrentSessionResponse.builder()
                    .userId(user != null ? user.getId() : ctx.getUserId())
                    .email(user != null ? user.getEmail() : null)
                    .firstName(user != null ? user.getFirstName() : null)
                    .lastName(user != null ? user.getLastName() : null)
                    .avatarUrl(user != null ? user.getAvatarUrl() : null)
                    .appId(ctx.getAppId())
                    .profileId(ctx.getProfileId())
                    .orgId(ctx.getOrgId())
                    .profiles(profiles.stream().map(p -> CurrentSessionResponse.ProfileSummary.builder()
                            .id(p.getId())
                            .profileSlug(p.getProfileSlug())
                            .organizationId(p.getOrganizationId())
                            .isActive(p.getIsActive())
                            .build()).toList())
                    .authorization(authorizationResolverService.resolveForCurrentProfile(ctx))
                    .build();
            return okBody(data, ctx);
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