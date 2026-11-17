package com.ayanlabs.security.config;

import com.ayanlabs.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/auth/api/apps/catalog").permitAll()
                .requestMatchers(HttpMethod.POST,
                        "/auth/api/register",
                        "/auth/api/login",
                        "/auth/api/refresh",
                        "/auth/api/confirm-email",
                        "/auth/api/confirm-sms",
                        "/auth/api/forgot-password",
                        "/auth/api/reset-password",
                        "/auth/api/forgot-username").permitAll()
                .requestMatchers(
                        "/*/api/auth/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/webjars/**",
                        "/actuator/**").permitAll()
                // IPMS public signup + health
                .requestMatchers(HttpMethod.POST, "/pms/api/signup/step-1/register").permitAll()
                .requestMatchers(HttpMethod.GET, "/pms/api/signup/step-1/check-email").permitAll()
                .requestMatchers(HttpMethod.GET, "/pms/api/signup/step-1/check-phone").permitAll()
                .requestMatchers(HttpMethod.GET, "/pms/api/health").permitAll()
                .requestMatchers(HttpMethod.GET, "/pms/api/subscriptions/plans").permitAll()
                // public subscription catalog (core) — no JWT required for GET
                .requestMatchers(HttpMethod.GET, "/core/api/subscription-plans/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/core/api/profile-subscriptions/catalog").permitAll()
                // allow CORS preflight
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .anyRequest().authenticated())
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
