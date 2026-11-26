package com.ayanlab.comm.backend.dto;

import java.util.Map;
import java.util.UUID;

public record ContactDto(
    UUID id,
    String email,
    String firstName,
    String lastName,
    String preferredLanguage,
    Map<String, Object> customAttributes,
    String status,
    String externalId
) {}
