package com.ayanlab.core.util;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Canonical permission code format: {@code {app}:{resource}:{action}}.
 * <p>
 * Grants may use {@code *} for resource and/or action. {@code manage} is an ordinary action: existing role
 * matrices grant it next to an explicit action list, so letting it imply other actions would widen access.
 * Legacy codes in the 4-segment {@code {app}:{module}:{resource}:{action}} shape are accepted via {@link #fromLegacy}.
 */
public final class PermissionCodes {

    public static final String WILDCARD = "*";
    public static final String MANAGE = "manage";

    public static final Set<String> ACTIONS = Set.of(
        "read", "create", "update", "delete", MANAGE,
        "approve", "review", "export", "import", "share", "search"
    );

    private static final String SEGMENT = "[a-z][a-z0-9_]*";
    private static final Pattern APP = Pattern.compile(SEGMENT);
    private static final Pattern RESOURCE = Pattern.compile(SEGMENT + "(\\." + SEGMENT + ")*");

    private PermissionCodes() {}

    public static String of(String app, String resource, String action) {
        return String.join(":", app, resource, action);
    }

    /** Concrete code (no wildcards) with a known action. */
    public static boolean isValid(String code) {
        String[] parts = split(code);
        return parts != null
            && APP.matcher(parts[0]).matches()
            && RESOURCE.matcher(parts[1]).matches()
            && ACTIONS.contains(parts[2]);
    }

    /** Code that may be granted to a principal: like {@link #isValid} but resource/action may be {@code *}. */
    public static boolean isValidGrant(String code) {
        String[] parts = split(code);
        return parts != null
            && APP.matcher(parts[0]).matches()
            && (WILDCARD.equals(parts[1]) || RESOURCE.matcher(parts[1]).matches())
            && (WILDCARD.equals(parts[2]) || ACTIONS.contains(parts[2]));
    }

    /** Maps {@code app:module:resource:action} to {@code app:resource:action}; v2 codes pass through. */
    public static Optional<String> fromLegacy(String code) {
        if (code == null) {
            return Optional.empty();
        }
        String[] parts = code.split(":", -1);
        if (parts.length == 4) {
            String converted = of(parts[0], parts[2], parts[3]);
            return isValidGrant(converted) ? Optional.of(converted) : Optional.empty();
        }
        return isValidGrant(code) ? Optional.of(code) : Optional.empty();
    }

    public static boolean implies(String grant, String required) {
        String[] g = split(grant);
        String[] r = split(required);
        if (g == null || r == null || !g[0].equals(r[0])) {
            return false;
        }
        boolean resourceMatches = WILDCARD.equals(g[1]) || g[1].equals(r[1]);
        boolean actionMatches = WILDCARD.equals(g[2]) || g[2].equals(r[2]);
        return resourceMatches && actionMatches;
    }

    public static boolean anyImplies(Collection<String> grants, String required) {
        if (grants == null || grants.isEmpty()) {
            return false;
        }
        String normalized = fromLegacy(required).orElse(null);
        if (normalized == null) {
            return false;
        }
        for (String grant : grants) {
            if (implies(grant, normalized)) {
                return true;
            }
        }
        return false;
    }

    private static String[] split(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        String[] parts = code.split(":", -1);
        return parts.length == 3 ? parts : null;
    }
}
