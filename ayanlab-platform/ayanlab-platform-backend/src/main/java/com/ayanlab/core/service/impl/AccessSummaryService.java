package com.ayanlab.core.service.impl;

import com.ayanlab.core.data.CoreContext;
import com.ayanlab.core.data.dto.EffectiveAuthorization;
import com.ayanlab.core.data.responses.AccessSummary;
import com.ayanlab.core.entity.Menu;
import com.ayanlab.core.entity.Organization;
import com.ayanlab.core.entity.Role;
import com.ayanlab.core.entity.RoleMenu;
import com.ayanlab.core.entity.UserProfile;
import com.ayanlab.core.exception.ResourceNotFoundException;
import com.ayanlab.core.repository.MenuRepository;
import com.ayanlab.core.repository.OrganizationRepository;
import com.ayanlab.core.repository.RoleMenuRepository;
import com.ayanlab.core.repository.RoleRepository;
import com.ayanlab.core.repository.UserProfileRepository;
import com.ayanlab.core.service.IAccessSummaryService;
import com.ayanlab.core.service.IAuthorizationResolverService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccessSummaryService implements IAccessSummaryService {

    private static final List<String> ROLE_PRIORITY = List.of(
        "SYSTEM_ADMINISTRATOR",
        "MAINTENANCE_ORGANIZATION",
        "ORGANIZATION_ADMINISTRATOR",
        "PROPERTY_MANAGER",
        "PROPERTY_OWNER",
        "VENDOR",
        "MAINTENANCE_STAFF",
        "TENANT",
        "ACCOUNTANT",
        "LEASING_AGENT",
        "SYSTEM_USER",
        "VIEWER"
    );

    private static final Map<String, String> FRONTEND_ROLE = Map.ofEntries(
        Map.entry("SYSTEM_ADMINISTRATOR", "system_administrator"),
        Map.entry("SYSTEM_USER", "system_user"),
        Map.entry("VIEWER", "system_user"),
        Map.entry("ORGANIZATION_ADMINISTRATOR", "property_management_admin"),
        Map.entry("PROPERTY_MANAGER", "property_management"),
        Map.entry("ACCOUNTANT", "property_management"),
        Map.entry("LEASING_AGENT", "property_management"),
        Map.entry("TENANT", "tenant"),
        Map.entry("PROPERTY_OWNER", "tenant"),
        Map.entry("MAINTENANCE_STAFF", "maintenance_staff"),
        Map.entry("VENDOR", "maintenance_staff"),
        Map.entry("MAINTENANCE_ORGANIZATION", "maintenance_organization")
    );

    private static final Map<String, List<String>> DEFAULT_POLICIES = Map.ofEntries(
        Map.entry("SYSTEM_ADMINISTRATOR", List.of("POL_SCOPE_SYSTEM")),
        Map.entry("SYSTEM_USER", List.of("POL_SCOPE_SYSTEM_READ")),
        Map.entry("VIEWER", List.of("POL_SCOPE_SYSTEM_READ")),
        Map.entry("ORGANIZATION_ADMINISTRATOR", List.of("POL_SCOPE_ORG")),
        Map.entry("PROPERTY_MANAGER", List.of("POL_SCOPE_ORG")),
        Map.entry("ACCOUNTANT", List.of("POL_SCOPE_ORG")),
        Map.entry("LEASING_AGENT", List.of("POL_SCOPE_ORG")),
        Map.entry("MAINTENANCE_ORGANIZATION", List.of("POL_SCOPE_EXTERNAL")),
        Map.entry("MAINTENANCE_STAFF", List.of("POL_SCOPE_ORG")),
        Map.entry("VENDOR", List.of("POL_SCOPE_SELF")),
        Map.entry("TENANT", List.of("POL_SCOPE_SELF")),
        Map.entry("PROPERTY_OWNER", List.of("POL_SCOPE_SELF"))
    );

    private static final Map<String, List<String>> DEFAULT_CAPABILITIES = Map.ofEntries(
        Map.entry("SYSTEM_ADMINISTRATOR", List.of(
            "manage_all", "manage_org_users", "define_custom_roles", "view_analytics", "manage_billing"
        )),
        Map.entry("SYSTEM_USER", List.of("view_users_readonly")),
        Map.entry("ORGANIZATION_ADMINISTRATOR", List.of(
            "manage_org_users", "define_custom_roles", "manage_billing", "view_analytics"
        )),
        Map.entry("MAINTENANCE_ORGANIZATION", List.of("manage_org_users", "define_custom_roles")),
        Map.entry("PROPERTY_MANAGER", List.of("view_analytics")),
        Map.entry("ACCOUNTANT", List.of("export_financials")),
        Map.entry("LEASING_AGENT", List.of()),
        Map.entry("TENANT", List.of("pay_rent", "submit_maintenance")),
        Map.entry("PROPERTY_OWNER", List.of("view_own_properties", "submit_maintenance")),
        Map.entry("VENDOR", List.of("update_work_orders")),
        Map.entry("MAINTENANCE_STAFF", List.of("update_work_orders")),
        Map.entry("VIEWER", List.of())
    );

    private final IAuthorizationResolverService authorizationResolverService;
    private final MenuRepository menuRepository;
    private final RoleMenuRepository roleMenuRepository;
    private final RoleRepository roleRepository;
    private final OrganizationRepository organizationRepository;
    private final UserProfileRepository userProfileRepository;

    @Override
    public AccessSummary buildForProfile(UUID profileId, CoreContext context) {
        UserProfile profile = userProfileRepository.findById(profileId)
            .orElseThrow(() -> new ResourceNotFoundException("UserProfile", "id", profileId.toString()));
        EffectiveAuthorization auth = authorizationResolverService.resolveForProfile(profileId, context);
        return build(auth, profile, null);
    }

    @Override
    public AccessSummary build(EffectiveAuthorization authorization, UserProfile profile, String orgCodeHint) {
        Set<String> roleCodes = authorization.getRoleCodes() != null
            ? new LinkedHashSet<>(authorization.getRoleCodes())
            : Set.of();
        Set<String> permissionCodes = authorization.getPermissionCodes() != null
            ? new LinkedHashSet<>(authorization.getPermissionCodes())
            : Set.of();

        String primary = pickPrimary(roleCodes);
        String frontendRole = primary == null || primary.isBlank()
            ? "tenant"
            : FRONTEND_ROLE.getOrDefault(primary, primary.toLowerCase());
        Map<String, List<String>> modules = modulesFromPermissions(permissionCodes);

        UUID appId = authorization.getAppId() != null
            ? authorization.getAppId()
            : (profile != null ? profile.getAppId() : null);
        List<String> menus = resolveMenus(appId, roleCodes);
        List<String> policies = resolvePolicies(roleCodes, authorization.getPolicyGroupCodes());
        List<String> capabilities = resolveCapabilities(roleCodes);

        String orgCode = orgCodeHint;
        UUID orgId = authorization.getOrganizationId() != null
            ? authorization.getOrganizationId()
            : (profile != null ? profile.getOrganizationId() : null);
        if ((orgCode == null || orgCode.isBlank()) && orgId != null) {
            orgCode = organizationRepository.findById(orgId).map(Organization::getCode).orElse(null);
        }

        String redirectTo = (orgCode != null && !orgCode.isBlank())
            ? "/" + orgCode + "/dashboard"
            : "/dashboard";

        return new AccessSummary(
            roleCodes,
            permissionCodes,
            primary,
            frontendRole,
            modules,
            menus,
            policies,
            capabilities,
            redirectTo,
            orgCode
        );
    }

    private List<String> resolveMenus(UUID appId, Set<String> roleCodes) {
        if (appId == null || roleCodes == null || roleCodes.isEmpty()) {
            return List.of();
        }
        try {
            List<Role> roles = roleRepository.findByIsActiveTrueOrderByCodeAsc().stream()
                .filter(r -> r.getId() != null && r.getCode() != null)
                .filter(r -> roleCodes.stream().anyMatch(c -> c.equalsIgnoreCase(r.getCode())))
                .toList();
            if (roles.isEmpty()) {
                return List.of();
            }
            Set<UUID> roleIds = roles.stream().map(Role::getId).collect(Collectors.toSet());
            List<RoleMenu> links = roleMenuRepository.findByRoleIdInAndIsActiveTrue(roleIds);
            if (links == null || links.isEmpty()) {
                return List.of();
            }
            Set<UUID> menuIds = links.stream()
                .map(RoleMenu::getMenuId)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
            if (menuIds.isEmpty()) {
                return List.of();
            }
            return menuRepository.findByAppIdAndIsActiveTrueOrderBySortOrderAscCodeAsc(appId).stream()
                .filter(m -> m.getId() != null && menuIds.contains(m.getId()))
                .map(Menu::getCode)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        } catch (Exception ex) {
            return List.of();
        }
    }

    private List<String> resolvePolicies(Set<String> roleCodes, Set<String> policyGroupCodes) {
        LinkedHashSet<String> out = new LinkedHashSet<>();
        if (policyGroupCodes != null) {
            policyGroupCodes.stream()
                .filter(c -> c != null && c.startsWith("POL_"))
                .forEach(out::add);
        }
        if (roleCodes != null) {
            for (String role : roleCodes) {
                if (role == null || role.isBlank()) {
                    continue;
                }
                List<String> defaults = DEFAULT_POLICIES.get(role.toUpperCase());
                if (defaults != null) {
                    out.addAll(defaults);
                }
            }
        }
        return List.copyOf(out);
    }

    private List<String> resolveCapabilities(Set<String> roleCodes) {
        LinkedHashSet<String> out = new LinkedHashSet<>();
        if (roleCodes != null) {
            for (String role : roleCodes) {
                if (role == null || role.isBlank()) {
                    continue;
                }
                List<String> caps = DEFAULT_CAPABILITIES.get(role.toUpperCase());
                if (caps != null) {
                    out.addAll(caps);
                }
            }
        }
        return List.copyOf(out);
    }

    private String pickPrimary(Set<String> roleCodes) {
        if (roleCodes == null || roleCodes.isEmpty()) {
            return null;
        }
        Set<String> upper = roleCodes.stream().map(String::toUpperCase).collect(Collectors.toCollection(LinkedHashSet::new));
        for (String code : ROLE_PRIORITY) {
            if (upper.contains(code)) {
                return code;
            }
        }
        return upper.iterator().next();
    }

    private Map<String, List<String>> modulesFromPermissions(Set<String> permissionCodes) {
        Map<String, LinkedHashSet<String>> modules = new LinkedHashMap<>();
        for (String code : permissionCodes) {
            String[] parts = code.split(":");
            if (parts.length < 4) {
                continue;
            }
            String module = parts[1];
            String action = parts[parts.length - 1];
            modules.computeIfAbsent(module, k -> new LinkedHashSet<>()).add(action);
        }
        Map<String, List<String>> out = new LinkedHashMap<>();
        modules.forEach((k, v) -> out.put(k, List.copyOf(v)));
        return out;
    }
}
