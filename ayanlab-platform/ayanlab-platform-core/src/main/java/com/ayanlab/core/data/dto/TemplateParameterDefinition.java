package com.ayanlab.core.data.dto;

import jakarta.validation.constraints.NotBlank;

public record TemplateParameterDefinition(
        @NotBlank String name,
        @NotBlank String type,
        boolean required,
        String defaultValue,
        String description
) {
}

