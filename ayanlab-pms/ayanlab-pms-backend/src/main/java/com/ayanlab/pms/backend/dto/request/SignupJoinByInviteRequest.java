package com.ayanlab.pms.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SignupJoinByInviteRequest(
    @NotBlank String inviteCode
) {}
