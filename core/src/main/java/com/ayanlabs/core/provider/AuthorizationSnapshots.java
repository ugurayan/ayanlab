package com.ayanlabs.core.provider;

import java.util.*;

/**
 * Builds a serializable snapshot of the current JWT-backed {@link AppSecurityContext} for module authorization APIs.
 */
public final class AuthorizationSnapshots {

    private AuthorizationSnapshots() {}

    public static Map<String, Object> currentPrincipal() {
        AppSecurityContext.AppContext ctx = AppSecurityContext.get();
        if (ctx == null) {
            return Map.of("authenticated", false);
        }
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("authenticated", true);
        m.put("userId", ctx.getUserId());
        m.put("profileId", ctx.getProfileId());
        m.put("appKey", ctx.getAppKey());
        m.put("organizationId", ctx.getOrgId());
        m.put("roles", ctx.getRoles() != null ? new ArrayList<>(ctx.getRoles()) : List.of());
        m.put("permissions", ctx.getPermissions() != null ? new ArrayList<>(ctx.getPermissions()) : List.of());
        return Collections.unmodifiableMap(m);
    }
}
