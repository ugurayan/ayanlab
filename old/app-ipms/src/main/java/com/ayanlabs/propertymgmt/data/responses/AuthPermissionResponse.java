package com.ayanlabs.propertymgmt.data.responses;

import java.util.UUID;

public record AuthPermissionResponse(
        UUID id,
        String code,
        String name,
        String description,
        String module,
        String resource,
        String action
) {
}
