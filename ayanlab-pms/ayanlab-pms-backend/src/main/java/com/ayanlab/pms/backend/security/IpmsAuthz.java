package com.ayanlab.pms.backend.security;

import com.ayanlab.core.exception.ForbiddenException;
import com.ayanlab.core.provider.AppSecurityContext;
import com.ayanlab.core.util.PermissionCodes;

/**
 * Runtime permission checks for IPMS CRUD ({@code ipms:{resource}:{action}}).
 */
public final class IpmsAuthz {

    private IpmsAuthz() {}

    public static void requireOperation(String operationPrefix, String action) {
        String resource = IpmsAuthzResources.resolve(operationPrefix);
        require("ipms", resource, action);
    }

    public static void require(String app, String resource, String action) {
        String code = PermissionCodes.of(app, resource, action);
        AppSecurityContext.AppContext ctx = AppSecurityContext.get();
        if (ctx == null) {
            throw new ForbiddenException("Authentication required");
        }
        if (!ctx.hasPermission(code)) {
            throw new ForbiddenException("Required permission: " + code);
        }
    }
}