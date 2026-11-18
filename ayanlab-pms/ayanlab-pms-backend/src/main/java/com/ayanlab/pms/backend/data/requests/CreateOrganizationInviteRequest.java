package com.ayanlab.pms.backend.data.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.UUID;

public record CreateOrganizationInviteRequest(
    @Email String invitedEmail,
    UUID invitedUserId,
    String roleCode,
    @Min(1) @Max(90) Integer expiresInDays
) {}
