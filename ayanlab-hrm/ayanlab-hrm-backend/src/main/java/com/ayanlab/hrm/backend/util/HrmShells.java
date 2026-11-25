package com.ayanlab.hrm.backend.util;

import com.ayanlab.core.provider.AppSecurityContext;
import com.ayanlab.hrm.backend.enums.HrmShell;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Active profile shells. A platform-wide grant opens every shell. An organization
 * role does not open the worker shell unless a worker role is also present.
 */
public final class HrmShells {

    private static final Set<String> EMPLOYER_ROLES = Set.of(
            "ORG_ADMIN", "ORG_FINANCE", "LOCATION_MANAGER", "RECRUITER",
            "BUSINESS_ADMIN", "BUSINESS_MEMBER", "EMPLOYER", "AGENT_ADMIN", "AGENT_USER",
            "4b029d24-e658-4a65-8d76-ddbc2627a05c",
            "055978b5-7e39-494d-9725-a0dca32dda6c",
            "b0633699-641c-47c5-89c6-6a88f7d83754");

    private static final Set<String> WORKER_ROLES = Set.of(
            "WORKER", "RIDER", "9c2b8be8-bdec-4484-96b6-a2145b654b4d");

    private static final Set<String> ADMIN_ROLES = Set.of(
            "PLATFORM_ADMIN", "ADMIN", "SYSTEM_ADMIN", "STAFF",
            "4a3207bf-cc00-46db-8db7-42e8d9568b8b",
            "a0fdb0b8-a079-4315-ae21-31e68cd80778");

    private HrmShells() {
    }

    public static List<String> available(AppSecurityContext.AppContext ctx) {
        if (ctx == null) {
            return List.of();
        }
        Set<String> roles = ctx.getRoles() == null ? Set.of() : ctx.getRoles();
        boolean wildcard = ctx.hasPermission("staffiq:*:*");
        boolean allShells = wildcard || containsAny(roles, Set.of("PLATFORM_ADMIN", "ADMIN"));
        boolean employerRole = containsAny(roles, EMPLOYER_ROLES);
        boolean workerRole = containsAny(roles, WORKER_ROLES);
        boolean adminRole = containsAny(roles, ADMIN_ROLES);
        boolean orgWork = ctx.hasPermission("staffiq:organizations:read")
                || ctx.hasPermission("staffiq:shifts:create");

        List<String> shells = new ArrayList<>();
        if (allShells || employerRole || orgWork) {
            shells.add(HrmShell.EMPLOYER.code());
        }
        if (allShells || workerRole || (ctx.hasPermission("staffiq:applications:create") && !orgWork)) {
            shells.add(HrmShell.WORKER.code());
        }
        if (allShells || adminRole || ctx.hasPermission("staffiq:job_roles:create")) {
            shells.add(HrmShell.ADMIN.code());
        }
        return List.copyOf(shells);
    }

    private static boolean containsAny(Set<String> roles, Set<String> expected) {
        for (String role : roles) {
            if (expected.contains(role)) {
                return true;
            }
        }
        return false;
    }
}
