package com.ayanlab.pms.backend.dto.response;

import com.ayanlab.pms.backend.enums.OrganizationJoinRequestStatusEnum;

import java.time.Instant;
import java.util.UUID;

public record OrganizationJoinRequestResponse(
    UUID joinRequestId,
    UUID organizationId,
    UUID userId,
    UUID profileId,
    String userEmail,
    String userFirstName,
    String userLastName,
    String message,
    OrganizationJoinRequestStatusEnum status,
    Instant createdAt,
    Instant reviewedAt,
    UUID reviewedBy
) {}
