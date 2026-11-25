package com.ayanlab.hrm.backend.util;

import com.ayanlab.core.dto.CoreContext;
import java.util.UUID;

public final class StaffingScope {

    private StaffingScope() {}

    public static UUID actor(CoreContext context) {
        return context == null ? null : context.getUserId();
    }

    public static UUID organizationId(CoreContext context, UUID requested) {
        if (context != null && context.getOrgId() != null) {
            if (requested != null && !requested.equals(context.getOrgId())) {
                throw StaffingRules.bad("platformOrganizationId does not match the current organization");
            }
            return context.getOrgId();
        }
        if (requested == null) {
            throw StaffingRules.bad("platformOrganizationId is required");
        }
        return requested;
    }

    public static void assertSameOrg(CoreContext context, UUID rowOrgId) {
        if (context != null && context.getOrgId() != null && rowOrgId != null && !context.getOrgId().equals(rowOrgId)) {
            throw StaffingRules.notFound("Resource not found");
        }
    }
}
