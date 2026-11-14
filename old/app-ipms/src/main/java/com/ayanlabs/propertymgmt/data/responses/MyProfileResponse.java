package com.ayanlabs.propertymgmt.data.responses;

import java.util.UUID;

public record MyProfileResponse(
        UUID userId,
        UUID profileId,
        UUID organizationId,
        String firstName,
        String lastName,
        String email,
        String phone,
        Boolean emailVerified,
        Boolean phoneVerified,
        Boolean fullNameVerified,
        String avatarUrl
) {
}
