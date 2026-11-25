package com.ayanlab.pms.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateAuthRoleRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 500) String description,
        UUID baseRoleId
) {
}
