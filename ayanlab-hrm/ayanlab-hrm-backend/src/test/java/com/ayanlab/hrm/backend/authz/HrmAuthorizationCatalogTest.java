package com.ayanlab.hrm.backend.authz;

import com.ayanlab.core.authz.catalog.AppAuthorizationCatalog;
import com.ayanlab.core.util.PermissionCodes;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.InputStream;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class HrmAuthorizationCatalogTest {

    private static AppAuthorizationCatalog catalog;
    private static List<String> declaredPermissions;

    @BeforeAll
    static void load() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        for (Resource resource : new PathMatchingResourcePatternResolver().getResources("classpath:authz/*.json")) {
            try (InputStream is = resource.getInputStream()) {
                AppAuthorizationCatalog part = mapper.readValue(is, AppAuthorizationCatalog.class);
                catalog = catalog == null ? part : catalog.merge(part);
            }
        }
        declaredPermissions = catalog.resourcesOrEmpty().stream()
            .flatMap(r -> r.actions().stream().map(a -> PermissionCodes.of(catalog.appCode(), r.code(), a)))
            .toList();
    }

    @Test
    void catalogIsValid() {
        assertThat(catalog.appCode()).isEqualTo("staffiq");
        assertThat(catalog.validate()).isEmpty();
    }

    @Test
    void platformAdminGetsEverything() {
        assertThat(permissionsOf("PLATFORM_ADMIN")).containsExactlyInAnyOrderElementsOf(declaredPermissions);
    }

    @Test
    void orgFinanceMatchesMock() {
        Set<String> perms = permissionsOf("ORG_FINANCE");
        assertThat(perms).contains("staffiq:finance:update", "staffiq:transactions:read", "staffiq:dashboard:read");
        assertThat(perms).doesNotContain(
                "staffiq:organizations:read", "staffiq:shifts:read", "staffiq:workers:read", "staffiq:education:read");
    }

    @Test
    void locationManagerCannotEditLocations() {
        Set<String> perms = permissionsOf("LOCATION_MANAGER");
        assertThat(perms).contains("staffiq:locations:read", "staffiq:shifts:create", "staffiq:applications:update");
        assertThat(perms).doesNotContain("staffiq:locations:update", "staffiq:finance:read");
    }

    @Test
    void workerOnlySeesWorkersThroughOverride() {
        assertThat(permissionsOf("WORKER")).doesNotContain("staffiq:workers:read", "staffiq:shifts:create");
        AppAuthorizationCatalog.UserSpec pending = catalog.usersOrEmpty().stream()
            .filter(u -> u.email().equals("notApproved@platform.test")).findFirst().orElseThrow();
        assertThat(pending.permissionOverrides())
            .extracting(AppAuthorizationCatalog.PermissionOverrideSpec::code)
            .containsExactly("staffiq:workers:read");
    }

    @Test
    void vendorUserReviewsDocumentsOnly() {
        Set<String> perms = permissionsOf("VENDOR_USER");
        assertThat(perms).contains("staffiq:documents:read", "staffiq:documents:review");
        assertThat(perms).noneMatch(p -> p.startsWith("staffiq:shifts:") || p.startsWith("staffiq:applications:"));
    }

    private static Set<String> permissionsOf(String roleCode) {
        Map<String, AppAuthorizationCatalog.PolicyGroupSpec> groups = new HashMap<>();
        catalog.policyGroupsOrEmpty().forEach(g -> groups.put(g.code(), g));
        AppAuthorizationCatalog.RoleSpec role = catalog.rolesOrEmpty().stream()
            .filter(r -> r.code().equals(roleCode)).findFirst().orElseThrow();
        Set<String> result = new TreeSet<>();
        for (String groupCode : role.policyGroups()) {
            AppAuthorizationCatalog.PolicyGroupSpec group = groups.get(groupCode);
            List<String> excludes = group.excludes() != null ? group.excludes() : List.of();
            for (String permission : declaredPermissions) {
                if (PermissionCodes.anyImplies(group.permissions(), permission)
                        && !PermissionCodes.anyImplies(excludes, permission)) {
                    result.add(permission);
                }
            }
        }
        return result;
    }
}
