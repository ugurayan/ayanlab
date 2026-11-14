package com.ayanlabs.propertymgmt.data.responses;

public record SignupFieldAvailabilityResponse(
    String field,
    String value,
    boolean available
) {}
