package com.ayanlabs.core.data;

/**
 * Standard client headers bound via {@link com.ayanlabs.core.annotation.ClientHeaders}.
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
