package com.ayanlab.pms.backend.data.responses;

import com.ayanlab.core.data.responses.AccessSummary;
import com.ayanlab.core.data.responses.TokenResponse;

public record SignupCompleteResponse(
    TokenResponse tokens,
    IpmsSignupSessionResponse session,
    String redirectTo,
    AccessSummary access
) {}
