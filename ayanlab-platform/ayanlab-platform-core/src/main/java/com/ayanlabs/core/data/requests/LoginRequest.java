package com.ayanlabs.core.data.requests;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.UUID;

@Data
public class LoginRequest {
    private String email;
    private String phone;
    @NotBlank private String password;
    /** Optional when X-App-Id header is present */
    private String appKey;
    private UUID orgId;
    /** Pick profile when user has multiple in the same app. */
    private UUID profileId;
    private String profileSlug;
}
