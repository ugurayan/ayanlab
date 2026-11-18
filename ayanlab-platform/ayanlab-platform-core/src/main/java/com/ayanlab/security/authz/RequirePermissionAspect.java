package com.ayanlab.security.authz;

import com.ayanlab.core.annotation.RequirePermission;
import com.ayanlab.core.exception.ForbiddenException;
import com.ayanlab.core.provider.AppSecurityContext;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Enforces {@link RequirePermission} on methods and classes. A method-level annotation replaces the class-level one.
 * Any listed permission is sufficient; grants are matched with wildcard / {@code manage} semantics.
 */
@Aspect
@Component
public class RequirePermissionAspect {

    @Before("@within(com.ayanlab.core.annotation.RequirePermission) || @annotation(com.ayanlab.core.annotation.RequirePermission)")
    public void checkPermission(JoinPoint jp) {
        RequirePermission requirement = AuthzAnnotations.effective(jp, RequirePermission.class);
        if (requirement == null) {
            return;
        }
        AppSecurityContext.AppContext ctx = AppSecurityContext.get();
        if (ctx == null) {
            throw new ForbiddenException("Authentication required");
        }
        boolean allowed = Arrays.stream(requirement.value()).anyMatch(ctx::hasPermission);
        if (!allowed) {
            throw new ForbiddenException("Required permission: " + Arrays.toString(requirement.value()));
        }
    }
}
