package com.ayanlab.pms.backend.data.requests;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record SignupJoinRequestRequest(
    @NotNull UUID organizationId,
    String message
) {}
