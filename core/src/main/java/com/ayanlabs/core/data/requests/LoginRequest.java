package com.ayanlabs.core.data.requests;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.UUID;

@Data
public class LoginRequest {
    private String email;
    private String phone;
    @NotBlank private String password;
    @NotBlank private String appKey;
    private UUID orgId;
}
