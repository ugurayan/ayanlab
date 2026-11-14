package com.ayanlabs.propertymgmt.data.requests;

import jakarta.validation.constraints.NotBlank;

public record SignupJoinByInviteRequest(
    @NotBlank String inviteCode
) {}
