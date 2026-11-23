package com.ayanlab.hrm.backend.service.support;

import com.ayanlab.core.data.CoreContext;
import com.ayanlab.core.exception.ForbiddenException;
import com.ayanlab.hrm.backend.entity.ProfileLinkedEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/** Shared profile/org stamping and row-level access checks for profile-linked HRM data. */
public final class HrmProfileContextSupport {

    private HrmProfileContextSupport() {}

    public static void stampFromContext(ProfileLinkedEntity body, CoreContext context) {
        if (context == null) {
            return;
        }
        if (context.getProfileId() != null
                && (body.getProfileId() == null || body.getProfileId().isBlank())) {
            body.setProfileId(context.getProfileId().toString());
        }
        if (context.getOrgId() != null && (body.getOrgId() == null || body.getOrgId().isBlank())) {
            body.setOrgId(context.getOrgId().toString());
        }
        if (context.getUserId() != null
                && (body.getPlatformUserId() == null || body.getPlatformUserId().isBlank())) {
            body.setPlatformUserId(context.getUserId().toString());
        }
    }

    public static void assertReadable(ProfileLinkedEntity row, CoreContext context) {
        if (context == null) {
            return;
        }
        if (context.getProfileId() != null
                && row.getProfileId() != null
                && !row.getProfileId().equals(context.getProfileId().toString())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        }
        if (context.getOrgId() != null
                && row.getOrgId() != null
                && !row.getOrgId().equals(context.getOrgId().toString())) {
            throw new ForbiddenException("Organization scope mismatch");
        }
    }
}