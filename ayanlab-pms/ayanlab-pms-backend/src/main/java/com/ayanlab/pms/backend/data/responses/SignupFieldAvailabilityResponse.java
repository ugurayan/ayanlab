package com.ayanlab.pms.backend.data.responses;

public record SignupFieldAvailabilityResponse(
    String field,
    String value,
    boolean available
) {}
