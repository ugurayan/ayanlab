package com.ayanlab.pms.backend.dto.response;

import java.time.Instant;
import java.util.UUID;

public record OrganizationInviteResponse(
    UUID inviteId,
    UUID organizationId,
    String inviteCode,
    String invitedEmail,
    UUID invitedUserId,
    String roleCode,
    Instant expiresAt,
    Instant createdAt
) {}
