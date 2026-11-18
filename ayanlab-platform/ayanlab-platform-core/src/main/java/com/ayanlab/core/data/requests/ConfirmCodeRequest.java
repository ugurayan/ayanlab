package com.ayanlab.core.data.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ConfirmCodeRequest(
    @NotNull UUID userId,
    @NotBlank String code
) {}

