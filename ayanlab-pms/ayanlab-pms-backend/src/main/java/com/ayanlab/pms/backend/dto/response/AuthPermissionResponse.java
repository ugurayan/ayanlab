package com.ayanlab.pms.backend.dto.response;

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
