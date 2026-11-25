package com.ayanlab.security.filter;

import com.ayanlab.core.constant.ApiClientHeaders;
import com.ayanlab.security.service.JwtTokenParser;
import com.ayanlab.core.provider.AppSecurityContext;
import com.ayanlab.core.provider.ResourceScopeClaims;
import com.ayanlab.security.authz.AuthzVersionChecker;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
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

    private final JwtTokenParser jwtService;
    private final ObjectProvider<AuthzVersionChecker> authzVersionChecker;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                     FilterChain chain) throws ServletException, IOException {
        String token = resolveAccessToken(request);
        if (token == null) {
            chain.doFilter(request, response);
            return;
        }
        if (!jwtService.isTokenValid(token)) {
            chain.doFilter(request, response);
            return;
        }

        Claims claims = jwtService.parseToken(token);
        String userId = claims.getSubject();
        String profileId = firstNonBlank(
            claims.get("profileId", String.class),
            claims.get("pid", String.class)
        );
        String appKey = claims.get("appKey", String.class);
        String orgId = firstNonBlank(
            claims.get("orgId", String.class),
            claims.get("oid", String.class)
        );

        AuthzVersionChecker versionChecker = authzVersionChecker.getIfAvailable();
        if (versionChecker != null && profileId != null
                && !versionChecker.isCurrent(appKey, UUID.fromString(profileId), claims.get(AuthzVersionChecker.CLAIM, String.class))) {
            response.setHeader(AuthzVersionChecker.STALE_HEADER, "true");
            chain.doFilter(request, response);
            return;
        }

        List<String> roles = toStringList(claims.get("roles"));
        List<String> permissions = toStringList(claims.get("permissions"));
        List<String> policyGroups = toStringList(claims.get("policyGroups"));

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
            .policyGroups(new HashSet<>(policyGroups != null ? policyGroups : List.of()))
            .resourceScopes(ResourceScopeClaims.fromClaim(claims.get(ResourceScopeClaims.CLAIM)))
            .build());

        try { chain.doFilter(request, response); }
        finally { AppSecurityContext.clear(); }
    }

    private static String resolveAccessToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String bearer = authHeader.substring(7).trim();
            if (!bearer.isEmpty()) {
                return bearer;
            }
        }
        String clientToken = request.getHeader(ApiClientHeaders.CLIENT_TOKEN);
        if (clientToken != null && !clientToken.isBlank()) {
            return clientToken.trim();
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // skip central auth and per-product auth under /{product}/api/auth/**
        if (path != null) {
            // Public auth routes are permitAll in SecurityConfig; JWT is optional there.
            if (path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs") || path.startsWith("/webjars/")
                || path.startsWith("/actuator")) return true;
            // module-specific auth (e.g. /vault/api/auth/...)
            if (path.matches("^/[^/]+/api/auth(/.*)?$")) return true;
            // IPMS public endpoints
            if ("/pms/api/health".equals(path)) return true;
            if ("/pms/api/subscriptions/plans".equals(path)) return true;
            if ("/pms/api/signup/step-1/register".equals(path)
                    && "POST".equalsIgnoreCase(request.getMethod())) return true;
            if ("/pms/api/signup/step-1/check-email".equals(path)
                    && "GET".equalsIgnoreCase(request.getMethod())) return true;
            if ("/pms/api/signup/step-1/check-phone".equals(path)
                    && "GET".equalsIgnoreCase(request.getMethod())) return true;
        }
        return false;
    }

    private static String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static List<String> toStringList(Object raw) {
        if (raw == null) {
            return List.of();
        }
        if (raw instanceof List<?> list) {
            return list.stream().map(String::valueOf).toList();
        }
        return List.of(String.valueOf(raw));
    }
}
