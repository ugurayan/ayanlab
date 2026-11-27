package com.ayanlab.core.authz.cache;

import java.util.UUID;

public final class ProfileAuthzCacheKeys {

    private ProfileAuthzCacheKeys() {
    }

    public static String key(String prefix, UUID profileId) {
        if (prefix == null || prefix.isBlank() || prefix.indexOf(':') >= 0) {
            throw new IllegalArgumentException("authz cache key prefix is required");
        }
        if (profileId == null) {
            throw new IllegalArgumentException("profile id is required");
        }
        return prefix.trim() + ":" + profileId;
    }
}
