package com.ayanlab.security.filter;

import com.ayanlab.core.provider.AppSecurityContext;
import com.ayanlab.security.authz.AuthzVersionChecker;
import com.ayanlab.security.service.JwtTokenParser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtAuthenticationFilterAuthzVersionTest {

    private static final UUID PROFILE = UUID.randomUUID();

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
        AppSecurityContext.clear();
    }

    @Test
    void currentTokenAuthenticates() throws Exception {
        Result result = run("3.7", "3.7");
        assertThat(result.permissions()).containsExactly("staffiq:shifts:read");
        assertThat(result.authzVersion()).isEqualTo("3.7");
        assertThat(result.response().getHeader(AuthzVersionChecker.STALE_HEADER)).isNull();
    }

    @Test
    void staleTokenIsNotAuthenticated() throws Exception {
        Result result = run("3.7", "4.7");
        assertThat(result.permissions()).isNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        assertThat(result.response().getHeader(AuthzVersionChecker.STALE_HEADER)).isEqualTo("true");
    }

    @Test
    void tokenWithoutVersionClaimIsAccepted() throws Exception {
        Result result = run(null, "4.7");
        assertThat(result.permissions()).containsExactly("staffiq:shifts:read");
    }

    private record Result(MockHttpServletResponse response, List<String> permissions, String authzVersion) {}

    private Result run(String tokenVersion, String currentVersion) throws Exception {
        var claimsBuilder = Jwts.claims()
            .subject(UUID.randomUUID().toString())
            .add("profileId", PROFILE.toString())
            .add("appKey", "staffiq")
            .add("permissions", List.of("staffiq:shifts:read"));
        if (tokenVersion != null) {
            claimsBuilder.add(AuthzVersionChecker.CLAIM, tokenVersion);
        }
        Claims claims = claimsBuilder.build();

        JwtTokenParser parser = mock(JwtTokenParser.class);
        when(parser.isTokenValid(anyString())).thenReturn(true);
        when(parser.parseToken(anyString())).thenReturn(claims);

        AuthzVersionChecker checker = (appCode, profileId) -> currentVersion;
        @SuppressWarnings("unchecked")
        ObjectProvider<AuthzVersionChecker> provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(checker);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/hrm/api/shifts");
        request.addHeader("Authorization", "Bearer token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<List<String>> seen = new AtomicReference<>();
        AtomicReference<String> version = new AtomicReference<>();
        MockFilterChain chain = new MockFilterChain(new jakarta.servlet.http.HttpServlet() {
            @Override
            protected void service(jakarta.servlet.http.HttpServletRequest req, jakarta.servlet.http.HttpServletResponse res) {
                AppSecurityContext.AppContext ctx = AppSecurityContext.get();
                seen.set(ctx != null ? List.copyOf(ctx.getPermissions()) : null);
                version.set(ctx != null ? ctx.getAuthzVersion() : null);
            }
        });

        new JwtAuthenticationFilter(parser, provider).doFilter(request, response, chain);
        return new Result(response, seen.get(), version.get());
    }
}
