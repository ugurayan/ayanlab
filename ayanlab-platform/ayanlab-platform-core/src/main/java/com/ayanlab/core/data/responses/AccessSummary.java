package com.ayanlab.core.data.responses;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Frontend session payload for authorization (login / signup-complete).
 * Menus are app-scoped codes from {@code core.Menu}; stored client-side after login.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AccessSummary(
    Set<String> roleCodes,
    Set<String> permissionCodes,
    String primaryRoleCode,
    String frontendRole,
    Map<String, List<String>> modules,
    List<String> menus,
    List<String> policies,
    List<String> capabilities,
    String redirectTo,
    String orgCode
) {}
