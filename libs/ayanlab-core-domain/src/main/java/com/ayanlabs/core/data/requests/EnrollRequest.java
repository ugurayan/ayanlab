package com.ayanlabs.core.data.requests;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.UUID;

@Data
public class EnrollRequest {
    @NotNull private UUID userId;
    @NotBlank private String appKey;
    @NotBlank @Email private String email;
    @NotBlank private String phone;
    private String roleKey;
    private UUID orgId;
}
