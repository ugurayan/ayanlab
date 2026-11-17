package com.ayanlabs.core.service.support;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.exception.CoreException;

import java.util.UUID;

public final class ProfileScopeSupport {

    private ProfileScopeSupport() {}

    public static UUID requireProfileId(CoreContext context) {
        if (context == null || context.getProfileId() == null) {
            throw new CoreException("VALIDATION_ERROR", "profileId required (X-Profile-Id or JWT)");
        }
        return context.getProfileId();
    }

    public static UUID resolveProfileId(UUID bodyProfileId, CoreContext context) {
        if (bodyProfileId != null) {
            return bodyProfileId;
        }
        return requireProfileId(context);
    }

    public static void assertProfileScope(UUID entityProfileId, CoreContext context) {
        if (entityProfileId == null || context.getProfileId() == null) {
            return;
        }
        if (!entityProfileId.equals(context.getProfileId())) {
            throw new CoreException("FORBIDDEN", "Resource does not belong to the current profile");
        }
    }

    public static void assertAppScope(UUID appId, CoreContext context) {
        if (context.getAppId() != null && appId != null && !context.getAppId().equals(appId)) {
            throw new CoreException("FORBIDDEN", "Resource does not belong to the current app");
        }
    }
}