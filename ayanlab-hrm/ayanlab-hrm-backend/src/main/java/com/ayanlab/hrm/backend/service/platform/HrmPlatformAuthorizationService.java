package com.ayanlab.hrm.backend.service.platform;

import com.ayanlab.core.authz.cache.ProfileAuthzCacheService;
import com.ayanlab.core.authz.cache.ProfileAuthzSnapshot;
import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.exception.ForbiddenException;
import com.ayanlab.core.provider.AppSecurityContext;
import com.ayanlab.core.util.PermissionCodes;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Backend-to-backend authorization. Reads the profile grant list from Redis
 * ({@code hrm:<profileId>}) and reloads it from the platform when the key is missing
 * or the token's authz version no longer matches.
 */
@Service
@RequiredArgsConstructor
public class HrmPlatformAuthorizationService {

    private final ProfileAuthzCacheService profileAuthzCacheService;

    @Value("${hrm.platform-auth.enabled:true}")
    private boolean enabled;

    public void requirePermission(CoreContext context, String requiredPermission) {
        if (!enabled) {
            return;
        }
        if (context == null || context.getToken() == null || context.getToken().isBlank()) {
            throw new ForbiddenException("Platform authorization required");
        }
        AppSecurityContext.AppContext security = AppSecurityContext.get();
        String tokenVersion = security != null ? security.getAuthzVersion() : null;
        ProfileAuthzSnapshot snapshot = profileAuthzCacheService.resolve(context, tokenVersion);
        Set<String> granted = snapshot.permissions() != null ? new LinkedHashSet<>(snapshot.permissions()) : Set.of();
        if (!PermissionCodes.anyImplies(granted, requiredPermission)) {
            throw new ForbiddenException("Platform denied permission: " + requiredPermission);
        }
    }
}
