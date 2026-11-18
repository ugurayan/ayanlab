package com.ayanlab.core.data.requests;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;
import java.util.UUID;

public record PreviewTemplateRequest(
        UUID appId,
        @NotBlank String templateCode,
        String language,
        Map<String, Object> params,
        Boolean strictValidation
) {
}

