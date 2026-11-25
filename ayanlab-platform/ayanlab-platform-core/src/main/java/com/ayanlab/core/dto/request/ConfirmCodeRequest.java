package com.ayanlab.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ConfirmCodeRequest(
    @NotNull UUID userId,
    @NotBlank String code
) {}

