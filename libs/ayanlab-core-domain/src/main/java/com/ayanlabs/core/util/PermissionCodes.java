package com.ayanlabs.core.util;

/**
 * Canonical permission code format: {@code {app}:{module}:{resource}:{action}}.
 */
public final class PermissionCodes {

    private PermissionCodes() {}

    public static String of(String app, String module, String resource, String action) {
        return String.join(":", app, module, resource, action);
    }

    public static boolean isValidFormat(String code) {
        if (code == null || code.isBlank()) {
            return false;
        }
        String[] parts = code.split(":");
        return parts.length == 4 && parts[0] != null && !parts[0].isBlank();
    }
}
