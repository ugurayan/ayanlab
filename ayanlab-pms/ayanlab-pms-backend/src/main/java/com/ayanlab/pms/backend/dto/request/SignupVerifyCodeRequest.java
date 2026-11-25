package com.ayanlab.pms.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record SignupVerifyCodeRequest(
    @NotBlank String code
) {}
