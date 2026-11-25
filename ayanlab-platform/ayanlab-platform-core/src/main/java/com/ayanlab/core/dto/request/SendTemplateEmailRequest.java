package com.ayanlab.core.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Map;
import java.util.UUID;

public record SendTemplateEmailRequest(
        @NotBlank @Email String to,
        UUID appId,
        @NotBlank String templateCode,
        String language,
        Map<String, Object> params,
        Boolean strictValidation
) {
}

