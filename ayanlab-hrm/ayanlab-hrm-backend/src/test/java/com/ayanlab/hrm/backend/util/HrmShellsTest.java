package com.ayanlab.hrm.backend.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.ayanlab.core.provider.AppSecurityContext;
import java.util.Set;
import org.junit.jupiter.api.Test;

class HrmShellsTest {

    @Test
    void organizationAdminStaysOnTheEmployerShell() {
        assertThat(HrmShells.available(ctx(
                Set.of("ORG_ADMIN"),
                Set.of("staffiq:organizations:read", "staffiq:shifts:create", "staffiq:applications:create"))))
                .containsExactly("employer");
    }

    @Test
    void workerRoleOpensOnlyTheWorkerShell() {
        assertThat(HrmShells.available(ctx(
                Set.of("WORKER"),
                Set.of("staffiq:applications:create", "staffiq:jobs:read"))))
                .containsExactly("worker");
    }

    @Test
    void bothRolesOpenEmployerAndWorker() {
        assertThat(HrmShells.available(ctx(
                Set.of("ORG_ADMIN", "WORKER"),
                Set.of("staffiq:organizations:read", "staffiq:applications:create"))))
                .containsExactly("employer", "worker");
    }

    @Test
    void platformGrantOpensEveryShell() {
        assertThat(HrmShells.available(ctx(Set.of("PLATFORM_ADMIN"), Set.of("staffiq:*:*"))))
                .containsExactly("employer", "worker", "admin");
    }

    @Test
    void platformRoleOpensEveryShellWithoutAWildcardGrant() {
        assertThat(HrmShells.available(ctx(Set.of("PLATFORM_ADMIN"), Set.of("staffiq:dashboard:read"))))
                .containsExactly("employer", "worker", "admin");
    }

    private static AppSecurityContext.AppContext ctx(Set<String> roles, Set<String> permissions) {
        return AppSecurityContext.AppContext.builder()
                .roles(roles)
                .permissions(permissions)
                .build();
    }
}
