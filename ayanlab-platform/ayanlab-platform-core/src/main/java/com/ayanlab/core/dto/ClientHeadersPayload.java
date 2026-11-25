package com.ayanlab.core.dto;

/**
 * Standard client headers bound via {@link com.ayanlab.core.annotation.ClientHeaders}.
 */
public record ClientHeadersPayload(
        String correlationId,
        String appId,
        String clientType,
        String refreshToken,
        String token,
        String language,
        String profileId,
        String orgId
) {
}
