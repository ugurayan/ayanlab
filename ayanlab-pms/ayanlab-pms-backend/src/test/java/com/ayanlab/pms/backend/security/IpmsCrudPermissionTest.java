package com.ayanlab.pms.backend.security;

import com.ayanlab.core.annotation.RequirePermission;
import com.ayanlab.core.exception.ForbiddenException;
import com.ayanlab.core.provider.AppSecurityContext;
import com.ayanlab.security.authz.RequirePermissionAspect;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Ensures platform {@link RequirePermissionAspect} works for IPMS dashboard annotations (B1.3).
 */
class IpmsCrudPermissionTest {

    @RequirePermission("ipms:dashboard:read")
    static class DashboardStub {
        public String load() {
            return "ok";
        }
    }

    @AfterEach
    void clear() {
        AppSecurityContext.clear();
    }

    @Test
    void dashboardProxy_requiresDashboardRead() {
        DashboardStub api = proxy(new DashboardStub());
        assertThatThrownBy(api::load).isInstanceOf(ForbiddenException.class);

        AppSecurityContext.set(AppSecurityContext.AppContext.builder()
            .permissions(Set.of("ipms:dashboard:read"))
            .build());
        assertThat(api.load()).isEqualTo("ok");
    }

    private static <T> T proxy(T target) {
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        factory.setProxyTargetClass(true);
        factory.addAspect(new RequirePermissionAspect());
        return factory.getProxy();
    }
}