package com.ayanlab.security.authz;

import com.ayanlab.core.annotation.RequireAppRole;
import com.ayanlab.core.exception.ForbiddenException;
import com.ayanlab.core.provider.AppSecurityContext;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/** Enforces {@link RequireAppRole}; prefer {@code @RequirePermission} for new endpoints. */
@Aspect
@Component
public class RequireAppRoleAspect {

    @Before("@within(com.ayanlab.core.annotation.RequireAppRole) || @annotation(com.ayanlab.core.annotation.RequireAppRole)")
    public void checkRole(JoinPoint jp) {
        RequireAppRole requirement = AuthzAnnotations.effective(jp, RequireAppRole.class);
        if (requirement == null) {
            return;
        }
        AppSecurityContext.AppContext ctx = AppSecurityContext.get();
        if (ctx == null) {
            throw new ForbiddenException("Authentication required");
        }
        boolean allowed = Arrays.stream(requirement.value()).anyMatch(ctx::hasRole);
        if (!allowed) {
            throw new ForbiddenException("Required role: " + Arrays.toString(requirement.value()));
        }
    }
}
