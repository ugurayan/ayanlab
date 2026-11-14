package com.ayanlabs.propertymgmt.data.responses;

import com.ayanlabs.core.data.responses.AccessSummary;
import com.ayanlabs.core.data.responses.TokenResponse;

public record SignupCompleteResponse(
    TokenResponse tokens,
    IpmsSignupSessionResponse session,
    String redirectTo,
    AccessSummary access
) {}
