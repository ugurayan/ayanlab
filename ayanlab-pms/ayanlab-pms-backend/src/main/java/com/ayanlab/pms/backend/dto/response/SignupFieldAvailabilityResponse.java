package com.ayanlab.pms.backend.dto.response;

public record SignupFieldAvailabilityResponse(
    String field,
    String value,
    boolean available
) {}
