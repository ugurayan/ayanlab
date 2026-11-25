package com.ayanlab.core.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ForgotRequest(
    @NotBlank String emailOrMobile
) {}

