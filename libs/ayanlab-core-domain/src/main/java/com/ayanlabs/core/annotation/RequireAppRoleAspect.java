package com.ayanlabs.core.annotation;

import com.ayanlabs.core.provider.AppSecurityContext;
import com.ayanlabs.core.exception.ForbiddenException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Aspect
@Component
public class RequireAppRoleAspect {

    @Before("@annotation(requireAppRole)")
    public void checkRole(JoinPoint jp, RequireAppRole requireAppRole) {
        AppSecurityContext.AppContext ctx = AppSecurityContext.get();
        if (ctx == null) throw new ForbiddenException("Authentication required");
        boolean hasRole = Arrays.stream(requireAppRole.value()).anyMatch(ctx::hasRole);
        if (!hasRole) throw new ForbiddenException("Required role: " + Arrays.toString(requireAppRole.value()));
    }
}
