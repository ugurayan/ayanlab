package com.ayanlab.security.authz;

import com.ayanlab.core.annotation.RequireAppRole;
import com.ayanlab.core.annotation.RequirePermission;
import com.ayanlab.core.exception.ForbiddenException;
import com.ayanlab.core.provider.AppSecurityContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequirePermissionAspectTest {

    @RequirePermission("core:roles:manage")
    static class RoleApi {
        @RequirePermission("core:roles:read")
        public String list() { return "list"; }

        public String create() { return "create"; }
    }

    static class MixedApi {
        @RequirePermission({"staffiq:shifts:create", "staffiq:shifts:update"})
        public String save() { return "save"; }

        @RequireAppRole("PLATFORM_ADMIN")
        public String admin() { return "admin"; }

        public String open() { return "open"; }
    }

    @AfterEach
    void clear() {
        AppSecurityContext.clear();
    }

    @Test
    void methodAnnotationOverridesClassAnnotation() {
        RoleApi api = proxy(new RoleApi());
        login(Set.of("core:roles:read"), Set.of());

        assertThat(api.list()).isEqualTo("list");
        assertThatThrownBy(api::create).isInstanceOf(ForbiddenException.class);
    }

    @Test
    void classAnnotationGuardsUnannotatedMethods() {
        RoleApi api = proxy(new RoleApi());
        login(Set.of("core:roles:manage"), Set.of());

        assertThat(api.create()).isEqualTo("create");
        assertThatThrownBy(api::list).isInstanceOf(ForbiddenException.class);
    }

    @Test
    void anyListedPermissionIsEnoughAndWildcardsMatch() {
        MixedApi api = proxy(new MixedApi());
        login(Set.of("staffiq:shifts:*"), Set.of());

        assertThat(api.save()).isEqualTo("save");
        assertThat(api.open()).isEqualTo("open");
    }

    @Test
    void missingContextIsForbidden() {
        MixedApi api = proxy(new MixedApi());

        assertThatThrownBy(api::save).isInstanceOf(ForbiddenException.class);
        assertThatThrownBy(api::admin).isInstanceOf(ForbiddenException.class);
        assertThat(api.open()).isEqualTo("open");
    }

    @Test
    void roleGuardChecksRoleCodes() {
        MixedApi api = proxy(new MixedApi());
        login(Set.of(), Set.of("ORG_ADMIN"));
        assertThatThrownBy(api::admin).isInstanceOf(ForbiddenException.class);

        login(Set.of(), Set.of("PLATFORM_ADMIN"));
        assertThat(api.admin()).isEqualTo("admin");
    }

    private static <T> T proxy(T target) {
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        factory.setProxyTargetClass(true);
        factory.addAspect(new RequirePermissionAspect());
        factory.addAspect(new RequireAppRoleAspect());
        return factory.getProxy();
    }

    private static void login(Set<String> permissions, Set<String> roles) {
        AppSecurityContext.set(AppSecurityContext.AppContext.builder()
            .userId(UUID.randomUUID())
            .permissions(permissions)
            .roles(roles)
            .build());
    }
}
