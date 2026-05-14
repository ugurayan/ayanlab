package com.ayanlabs.security.filter;

import com.ayanlabs.security.service.JwtService;
import com.ayanlabs.core.provider.AppSecurityContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.*;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                     FilterChain chain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response); return;
        }

        String token = authHeader.substring(7);
        if (!jwtService.isTokenValid(token)) {
            chain.doFilter(request, response); return;
        }

        Claims claims = jwtService.parseToken(token);
        String userId = claims.getSubject();
        String profileId = claims.get("profileId", String.class);
        String appKey = claims.get("appKey", String.class);
        List<String> roles = claims.get("roles", List.class);
        List<String> permissions = claims.get("permissions", List.class);
        String orgId = claims.get("orgId", String.class);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (roles != null) roles.forEach(r -> authorities.add(new SimpleGrantedAuthority("ROLE_" + r)));
        if (permissions != null) permissions.forEach(p -> authorities.add(new SimpleGrantedAuthority(p)));

        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(userId, null, authorities));

        AppSecurityContext.set(AppSecurityContext.AppContext.builder()
            .userId(UUID.fromString(userId))
            .profileId(profileId != null ? UUID.fromString(profileId) : null)
            .appKey(appKey)
            .orgId(orgId != null ? UUID.fromString(orgId) : null)
            .roles(new HashSet<>(roles != null ? roles : List.of()))
            .permissions(new HashSet<>(permissions != null ? permissions : List.of()))
            .build());

        try { chain.doFilter(request, response); }
        finally { AppSecurityContext.clear(); }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // skip root auth path and module-specific auth paths like /ayanlabs/api/{module}/auth/**
        if (path != null) {
            if (path.startsWith("/ayanlabs/api/auth/") || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs") || path.startsWith("/actuator")) return true;
            // module-specific auth (e.g. /ayanlabs/api/vault/auth/...)
            if (path.matches("^/ayanlabs/api/[^/]+/auth(/.*)?$") || path.matches("^/ayanlabs/api/[^/]+/auth/.*")) return true;
        }
        return false;
    }
}
