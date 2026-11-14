package com.ayanlabs.propertymgmt.data.responses;

import java.util.List;
import java.util.UUID;

public record AuthRoleResponse(
        UUID id,
        String code,
        String name,
        String description,
        boolean custom,
        int permissionCount,
        List<String> permissionCodes
) {
}
