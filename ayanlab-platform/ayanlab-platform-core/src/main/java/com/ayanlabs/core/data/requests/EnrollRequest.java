package com.ayanlabs.core.data.requests;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.UUID;

@Data
public class EnrollRequest {
    /** When omitted, uses authenticated user from JWT / session context. */
    private UUID userId;
    @NotBlank private String appKey;
    @NotBlank @Email private String email;
    private String phone;
    /** Role code to assign on the new profile (e.g. TENANT, ORGANIZATION_ADMINISTRATOR). */
    private String roleKey;
    private UUID orgId;
    /** Unique per user+app; default derived from org + roleKey. */
    private String profileSlug;
    private String profileName;
    private String userTypeCode;
}
