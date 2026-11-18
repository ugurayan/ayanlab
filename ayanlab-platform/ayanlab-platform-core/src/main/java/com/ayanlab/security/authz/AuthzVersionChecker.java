package com.ayanlab.security.authz;

import java.util.UUID;

/**
 * Decides whether an access token's authorization snapshot is still current.
 * Tokens carry {@link #CLAIM} = {@code "{appVersion}.{profileVersion}"}; the platform bumps the app version on
 * role / policy-group / permission changes and the profile version on profile grant changes.
 */
public interface AuthzVersionChecker {

    String CLAIM = "av";

    /** Response header set when a token was rejected as stale; clients should refresh their token. */
    String STALE_HEADER = "X-Authz-Stale";

    /** @return the current version for the app/profile pair, or {@code null} when unknown (treated as current). */
    String currentVersion(String appCode, UUID profileId);

    default boolean isCurrent(String appCode, UUID profileId, String tokenVersion) {
        if (tokenVersion == null || appCode == null || profileId == null) {
            return true;
        }
        String current = currentVersion(appCode, profileId);
        return current == null || current.equals(tokenVersion);
    }

    static String format(Long appVersion, Long profileVersion) {
        return (appVersion != null ? appVersion : 0L) + "." + (profileVersion != null ? profileVersion : 0L);
    }
}
