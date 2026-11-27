package com.ayanlab.core.authz.cache;

import com.ayanlab.core.dto.EffectiveAuthorization;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class ProfileAuthzSnapshots {

    private ProfileAuthzSnapshots() {
    }

    public static ProfileAuthzSnapshot from(EffectiveAuthorization authorization, Instant expiresAt) {
        return new ProfileAuthzSnapshot(
                authorization.getProfileId(),
                sorted(authorization.getPermissionCodes()),
                sorted(authorization.getRoleCodes()),
                sorted(authorization.getPolicyGroupCodes()),
                authorization.getAuthzVersion(),
                expiresAt);
    }

    static boolean reusable(ProfileAuthzSnapshot snapshot, String tokenVersion) {
        if (snapshot == null) {
            return false;
        }
        if (tokenVersion == null || tokenVersion.isBlank()) {
            return true;
        }
        return tokenVersion.equals(snapshot.authzVersion());
    }

    private static List<String> sorted(Set<String> values) {
        if (values == null || values.isEmpty()) {
            return List.of();
        }
        return values.stream().filter(Objects::nonNull).sorted().toList();
    }
}
