package com.ayanlabs.propertymgmt.data.responses;

import java.util.List;
import java.util.UUID;

public record OrgMemberResponse(
        UUID profileId,
        UUID userId,
        UUID organizationId,
        String firstName,
        String lastName,
        String name,
        String email,
        String phone,
        Boolean emailVerified,
        Boolean phoneVerified,
        UUID primaryRoleId,
        String primaryRoleCode,
        String primaryRoleName,
        List<String> roleCodes
) {
}
