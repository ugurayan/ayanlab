package com.ayanlabs.core.provider;

import com.ayanlabs.core.data.dto.ResourceScopeDto;
import com.ayanlabs.core.data.enums.ResourceScopeTypeEnum;

import java.util.Set;
import java.util.UUID;

/**
 * Helpers for row-level scope checks in app services.
 * When no scopes are configured for the current principal, org-wide access is assumed.
 */
public final class AuthorizationScopeSupport {

    private AuthorizationScopeSupport() {}

    public static boolean hasUnrestrictedAccess() {
        AppSecurityContext.AppContext ctx = AppSecurityContext.get();
        return ctx == null || ctx.getResourceScopes() == null || ctx.getResourceScopes().isEmpty();
    }

    public static boolean canAccess(ResourceScopeTypeEnum resourceType, UUID resourceId) {
        if (resourceType == null || resourceId == null) {
            return false;
        }
        if (hasUnrestrictedAccess()) {
            return true;
        }
        AppSecurityContext.AppContext ctx = AppSecurityContext.get();
        return ctx.getResourceScopes().stream()
            .anyMatch(scope -> resourceType.equals(scope.getResourceType())
                && resourceId.equals(scope.getResourceId()));
    }

    public static Set<UUID> allowedResourceIds(ResourceScopeTypeEnum resourceType) {
        AppSecurityContext.AppContext ctx = AppSecurityContext.get();
        if (ctx == null || ctx.getResourceScopes() == null) {
            return Set.of();
        }
        return ctx.getResourceScopes().stream()
            .filter(scope -> resourceType.equals(scope.getResourceType()))
            .map(ResourceScopeDto::getResourceId)
            .collect(java.util.stream.Collectors.toUnmodifiableSet());
    }
}
