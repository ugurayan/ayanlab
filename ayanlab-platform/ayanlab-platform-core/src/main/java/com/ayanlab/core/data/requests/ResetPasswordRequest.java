package com.ayanlab.core.data.requests;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
    @NotBlank String token,
    @NotBlank String newPassword
) {}

