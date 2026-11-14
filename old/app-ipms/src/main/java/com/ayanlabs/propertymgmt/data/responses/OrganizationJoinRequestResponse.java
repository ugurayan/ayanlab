package com.ayanlabs.propertymgmt.data.responses;

import com.ayanlabs.propertymgmt.data.enums.OrganizationJoinRequestStatusEnum;

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
