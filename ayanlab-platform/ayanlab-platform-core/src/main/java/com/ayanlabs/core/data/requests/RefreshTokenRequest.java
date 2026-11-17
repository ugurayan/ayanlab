package com.ayanlabs.core.data.requests;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RefreshTokenRequest {
    @NotBlank private String refreshToken;
    /** Optional when X-App-Id header is present */
    private String appKey;
}
