package com.ayanlabs.core.annotation;

import com.ayanlabs.core.exception.ForbiddenException;
import com.ayanlabs.core.provider.AppSecurityContext;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class RequirePermissionAspect {

    @Before("@annotation(requirePermission)")
    public void checkPermission(JoinPoint jp, RequirePermission requirePermission) {
        AppSecurityContext.AppContext ctx = AppSecurityContext.get();
        if (ctx == null) {
            throw new ForbiddenException("Authentication required");
        }
        boolean allowed = Arrays.stream(requirePermission.value()).anyMatch(ctx::hasPermission);
        if (!allowed) {
            throw new ForbiddenException("Required permission: " + Arrays.toString(requirePermission.value()));
        }
    }
}
