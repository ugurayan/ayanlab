package com.ayanlabs.propertymgmt.data.requests;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record SignupVerifyCodeRequest(
    @NotBlank String code
) {}
