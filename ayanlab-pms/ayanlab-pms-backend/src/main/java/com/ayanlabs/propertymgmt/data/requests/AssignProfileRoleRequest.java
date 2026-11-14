package com.ayanlabs.propertymgmt.data.requests;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignProfileRoleRequest(
        @NotNull UUID roleId
) {
}
