package com.ayanlab.pms.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.controller.BaseController;
import com.ayanlab.core.dto.ClientHeadersPayload;
import com.ayanlab.core.dto.request.ChangePasswordRequest;
import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.core.exception.CoreException;
import com.ayanlab.pms.backend.dto.request.UpdateMyPasswordRequest;
import com.ayanlab.security.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/pms/api/settings")
@Tag(name = "IPMS · SettingsController", description = "My settings & security operations")
@RequiredArgsConstructor
public class SettingsController extends BaseController {

    private static final String BASE_PATH = "/pms/api/settings";

    private final AuthService authService;

    @PostMapping(value = "/security/change-password", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Boolean>> changeMyPassword(
            @Valid @RequestBody UpdateMyPasswordRequest request,
            @ClientHeaders ClientHeadersPayload headers
    ) {
        TracedCall call = tracedCall(log, "change-my-password", "POST", BASE_PATH + "/security/change-password", headers, request, null);
        try {
            UUID userId = call.context().getUserId();
            if (userId == null) {
                throw new CoreException("UNAUTHORIZED", "Authenticated user required");
            }
            validatePasswordInputs(request);

            ChangePasswordRequest coreRequest = new ChangePasswordRequest(
                    userId,
                    request.currentPassword(),
                    request.newPassword()
            );
            boolean data = authService.changePassword(
                    coreRequest.userId(),
                    coreRequest.oldPassword(),
                    coreRequest.newPassword(),
                    call.context()
            );
            call.trace().success(Map.of("userId", userId, "success", data));
            return okBody(data, "Password updated successfully", call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    private static void validatePasswordInputs(UpdateMyPasswordRequest request) {
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new CoreException("VALIDATION_ERROR", "New password and confirm password must match");
        }
        if (request.currentPassword().equals(request.newPassword())) {
            throw new CoreException("VALIDATION_ERROR", "New password must be different from current password");
        }
        // Basic strength policy for security form.
        String password = request.newPassword();
        if (!password.matches(".*[A-Z].*")
                || !password.matches(".*[a-z].*")
                || !password.matches(".*\\d.*")
                || !password.matches(".*[^A-Za-z0-9].*")) {
            throw new CoreException(
                    "WEAK_PASSWORD",
                    "Password must include uppercase, lowercase, number, and special character"
            );
        }
    }
}
