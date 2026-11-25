package com.ayanlab.pms.backend.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignProfileRoleRequest(
        @NotNull UUID roleId
) {
}
