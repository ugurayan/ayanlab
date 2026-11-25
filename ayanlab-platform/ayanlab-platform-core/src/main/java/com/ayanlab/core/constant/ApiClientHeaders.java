package com.ayanlab.core.constant;

/**
 * Standard HTTP headers for every AyanLabs controller endpoint.
 * Bound via {@link com.ayanlab.core.annotation.ClientHeaders} / {@link com.ayanlab.core.dto.ClientHeadersPayload}.
 * See {@code CONTROLLER_API_HEADERS.md} at the repository root.
 */
public final class ApiClientHeaders {

    public static final String CORRELATION_ID = "X-Correlation-Id";
    public static final String APP_ID = "X-App-Id";
    public static final String CLIENT_LANGUAGE = "X-Client-Language";
    public static final String CLIENT_REFRESH_TOKEN = "X-Client-Refresh-Token";
    public static final String CLIENT_TOKEN = "X-Client-Token";
    public static final String CLIENT_TYPE = "X-Client-Type";
    public static final String PROFILE_ID = "X-Profile-Id";
    public static final String ORG_ID = "X-Org-Id";

    private ApiClientHeaders() {
    }
}
