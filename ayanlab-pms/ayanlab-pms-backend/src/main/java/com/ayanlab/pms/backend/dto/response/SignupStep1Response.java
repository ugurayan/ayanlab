package com.ayanlab.pms.backend.dto.response;

import java.util.UUID;

public record SignupStep1Response(
    UUID signupSessionId,
    UUID userId,
    UUID profileId,
    String accessToken,
    String refreshToken,
    boolean emailVerificationSent,
    boolean mobileVerificationSent,
    /** Present when email/SMS delivery is not configured (dev/testing). */
    String debugEmailCode,
    String debugMobileCode
) {
    public SignupStep1Response(
            UUID signupSessionId,
            UUID userId,
            UUID profileId,
            String accessToken,
            String refreshToken,
            boolean emailVerificationSent,
            boolean mobileVerificationSent) {
        this(signupSessionId, userId, profileId, accessToken, refreshToken,
                emailVerificationSent, mobileVerificationSent, null, null);
    }
}
