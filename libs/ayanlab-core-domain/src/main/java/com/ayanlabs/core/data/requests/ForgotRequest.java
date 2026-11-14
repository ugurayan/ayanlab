package com.ayanlabs.core.data.requests;

import jakarta.validation.constraints.NotBlank;

public record ForgotRequest(
    @NotBlank String emailOrMobile
) {}

