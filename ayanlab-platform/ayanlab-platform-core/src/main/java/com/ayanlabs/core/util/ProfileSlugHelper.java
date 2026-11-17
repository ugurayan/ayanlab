package com.ayanlabs.core.util;

import java.util.UUID;

public final class ProfileSlugHelper {

    private ProfileSlugHelper() {}

    public static String defaultSlug(UUID organizationId, String suffix) {
        String base = organizationId != null ? "org-" + organizationId : "personal";
        if (suffix == null || suffix.isBlank()) {
            return base;
        }
        return base + "-" + suffix;
    }

    public static String defaultSlug(UUID organizationId) {
        return defaultSlug(organizationId, null);
    }
}