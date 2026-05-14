package com.ayanlabs.core.data.requests;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RefreshTokenRequest {
    @NotBlank private String refreshToken;
    @NotBlank private String appKey;
}
