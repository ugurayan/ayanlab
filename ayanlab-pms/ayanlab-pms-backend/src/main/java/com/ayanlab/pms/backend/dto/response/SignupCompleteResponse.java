package com.ayanlab.pms.backend.dto.response;

import com.ayanlab.core.dto.response.AccessSummary;
import com.ayanlab.core.dto.response.TokenResponse;

public record SignupCompleteResponse(
    TokenResponse tokens,
    IpmsSignupSessionResponse session,
    String redirectTo,
    AccessSummary access
) {}
