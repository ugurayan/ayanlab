package com.ayanlab.core.constant;

public final class InternalApiHeaders {

    /** Shared secret for service-to-service calls ({@code PLATFORM_INTERNAL_TOKEN}). */
    public static final String INTERNAL_TOKEN = "X-Internal-Token";

    public static final String CATALOG_PATH = "/core/api/internal/authorization-catalogs";

    public static final String AUTHZ_VERSION_PATH = "/core/api/internal/authz-versions";

    private InternalApiHeaders() {}
}
