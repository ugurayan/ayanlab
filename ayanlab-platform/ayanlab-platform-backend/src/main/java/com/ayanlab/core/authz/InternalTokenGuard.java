package com.ayanlab.core.authz;

import com.ayanlab.core.exception.ForbiddenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/** Verifies {@code X-Internal-Token} on service-to-service endpoints; rejects everything when no token is configured. */
@Component
public class InternalTokenGuard {

    @Value("${platform.internal-token:}")
    private String internalToken;

    public void require(String presented) {
        if (internalToken == null || internalToken.isBlank()) {
            throw new ForbiddenException("Internal API disabled: platform.internal-token is not configured");
        }
        if (presented == null || !MessageDigest.isEqual(
                internalToken.getBytes(StandardCharsets.UTF_8), presented.getBytes(StandardCharsets.UTF_8))) {
            throw new ForbiddenException("Invalid internal token");
        }
    }
}
