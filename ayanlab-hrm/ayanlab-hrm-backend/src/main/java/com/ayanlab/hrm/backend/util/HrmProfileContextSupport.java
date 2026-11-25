package com.ayanlab.hrm.backend.util;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.exception.ForbiddenException;
import com.ayanlab.hrm.backend.entity.ProfileLinkedEntity;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/** Shared profile/org stamping and row-level access checks for profile-linked HRM data. */
public final class HrmProfileContextSupport {

    private HrmProfileContextSupport() {}

    public static void stampFromContext(ProfileLinkedEntity body, CoreContext context) {
        if (context == null) {
            return;
        }
        if (context.getProfileId() != null && body.getProfileId() == null) {
            body.setProfileId(context.getProfileId());
        }
        if (context.getOrgId() != null && body.getOrgId() == null) {
            body.setOrgId(context.getOrgId());
        }
        if (context.getUserId() != null && body.getPlatformUserId() == null) {
            body.setPlatformUserId(context.getUserId());
        }
    }

    public static void assertReadable(ProfileLinkedEntity row, CoreContext context) {
        if (context == null) {
            return;
        }
        if (context.getProfileId() != null
                && row.getProfileId() != null
                && !Objects.equals(row.getProfileId(), context.getProfileId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        }
        if (context.getOrgId() != null
                && row.getOrgId() != null
                && !Objects.equals(row.getOrgId(), context.getOrgId())) {
            throw new ForbiddenException("Organization scope mismatch");
        }
    }
}
