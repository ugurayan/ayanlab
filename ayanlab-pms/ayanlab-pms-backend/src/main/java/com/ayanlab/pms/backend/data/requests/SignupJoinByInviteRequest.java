package com.ayanlab.pms.backend.data.requests;

import jakarta.validation.constraints.NotBlank;

public record SignupJoinByInviteRequest(
    @NotBlank String inviteCode
) {}
