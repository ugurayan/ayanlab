package com.ayanlab.core.service.impl;

import com.ayanlab.core.authz.catalog.AppAuthorizationCatalog;
import com.ayanlab.core.authz.catalog.AppAuthorizationCatalog.*;
import com.ayanlab.core.data.enums.PermissionEffectEnum;
import com.ayanlab.core.data.enums.ProfileTenancyEnum;
import com.ayanlab.core.data.enums.RegistrationStatusEnum;
import com.ayanlab.core.entity.*;
import com.ayanlab.core.exception.CoreException;
import com.ayanlab.core.repository.*;
import com.ayanlab.core.util.IdGenerator;
import com.ayanlab.core.util.PermissionCodes;
import com.ayanlab.core.util.ProfileSlugHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

/**
 * Idempotent import of an {@link AppAuthorizationCatalog}. Upserts by natural key, only adds links
 * (never removes), and never overwrites passwords of existing users.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizationCatalogImportService {

    private final AppRepository appRepository;
    private final OrganizationRepository organizationRepository;
    private final ApplicationOrganizationRepository applicationOrganizationRepository;
    private final PermissionRepository permissionRepository;
    private final ApplicationPermissionRepository applicationPermissionRepository;
    private final PolicyGroupRepository policyGroupRepository;
    private final PolicyGroupPermissionRepository policyGroupPermissionRepository;
    private final RoleRepository roleRepository;
    private final RolePolicyGroupRepository rolePolicyGroupRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository profileRepository;
    private final ApplicationUserRepository applicationUserRepository;
    private final OrganizationUserRepository organizationUserRepository;
    private final ProfileRoleRepository profileRoleRepository;
    private final ProfilePolicyGroupRepository profilePolicyGroupRepository;
    private final ProfilePermissionRepository profilePermissionRepository;
    private final PasswordEncoder passwordEncoder;

    public record ImportResult(String appCode, int organizations, int permissions, int policyGroups,
                               int policyGroupLinks, int roles, int users, int userGrants) {}

    @Transactional
    public ImportResult importCatalog(AppAuthorizationCatalog catalog) {
        List<String> errors = catalog.validate();
        if (!errors.isEmpty()) {
            throw new CoreException("VALIDATION_ERROR", "Invalid authorization catalog: " + String.join("; ", errors));
        }
        App app = appRepository.findByCode(catalog.appCode())
            .orElseThrow(() -> new CoreException("NOT_FOUND", "App not found: " + catalog.appCode()));

        Counter c = new Counter();
        Map<String, Organization> orgs = importOrganizations(app, catalog, c);
        importPermissions(app, catalog, c);
        Map<String, PolicyGroup> groups = importPolicyGroups(app, catalog, c);
        Map<String, Role> roles = importRoles(app, catalog, groups, c);
        importUsers(app, catalog, orgs, roles, groups, c);

        ImportResult result = new ImportResult(app.getCode(), c.organizations, c.permissions, c.policyGroups,
            c.policyGroupLinks, c.roles, c.users, c.userGrants);
        log.info("[AuthorizationCatalogImport] {}", result);
        return result;
    }

    private Map<String, Organization> importOrganizations(App app, AppAuthorizationCatalog catalog, Counter c) {
        Map<String, Organization> byCode = new HashMap<>();
        for (OrganizationSpec spec : catalog.organizationsOrEmpty()) {
            Organization org = organizationRepository.findFirstByCode(spec.code()).orElseGet(() -> {
                Organization created = Organization.builder().code(spec.code()).name(spec.name()).description(spec.description()).build();
                IdGenerator.assignIfAbsent(created);
                c.organizations++;
                return organizationRepository.save(created);
            });
            if (!applicationOrganizationRepository.existsByAppIdAndOrgId(app.getId(), org.getId())) {
                ApplicationOrganization link = ApplicationOrganization.builder().appId(app.getId()).orgId(org.getId()).build();
                IdGenerator.assignIfAbsent(link);
                applicationOrganizationRepository.save(link);
            }
            byCode.put(spec.code(), org);
        }
        return byCode;
    }

    private void importPermissions(App app, AppAuthorizationCatalog catalog, Counter c) {
        Set<UUID> linked = new HashSet<>();
        applicationPermissionRepository.findByAppId(app.getId()).forEach(ap -> linked.add(ap.getPermissionId()));
        for (ResourceSpec resource : catalog.resourcesOrEmpty()) {
            for (String action : resource.actions()) {
                String code = PermissionCodes.of(app.getCode(), resource.code(), action);
                Permission permission = permissionRepository.findFirstByCode(code).orElseGet(() -> {
                    Permission created = Permission.builder()
                        .code(code)
                        .name(capitalize(action) + " " + (resource.name() != null ? resource.name() : resource.code()))
                        .appCode(app.getCode())
                        .module(resource.module() != null ? resource.module() : resource.code())
                        .resource(resource.code())
                        .action(action)
                        .build();
                    IdGenerator.assignIfAbsent(created);
                    c.permissions++;
                    return permissionRepository.save(created);
                });
                if (linked.add(permission.getId())) {
                    ApplicationPermission link = ApplicationPermission.builder().appId(app.getId()).permissionId(permission.getId()).build();
                    IdGenerator.assignIfAbsent(link);
                    applicationPermissionRepository.save(link);
                }
            }
        }
    }

    private Map<String, PolicyGroup> importPolicyGroups(App app, AppAuthorizationCatalog catalog, Counter c) {
        List<Permission> appPermissions = permissionRepository.findByAppCodeAndIsActiveTrueOrderByCodeAsc(app.getCode());
        Map<String, PolicyGroup> byCode = new HashMap<>();
        for (PolicyGroupSpec spec : catalog.policyGroupsOrEmpty()) {
            PolicyGroup group = policyGroupRepository.findByAppIdAndCode(app.getId(), spec.code()).orElseGet(() -> {
                PolicyGroup created = PolicyGroup.builder().appId(app.getId()).code(spec.code()).name(spec.name()).description(spec.description()).build();
                IdGenerator.assignIfAbsent(created);
                c.policyGroups++;
                return policyGroupRepository.save(created);
            });
            byCode.put(spec.code(), group);

            List<String> includes = spec.permissions() != null ? spec.permissions() : List.of();
            List<String> excludes = spec.excludes() != null ? spec.excludes() : List.of();
            for (Permission permission : appPermissions) {
                String code = permission.getCode();
                boolean included = includes.stream().anyMatch(p -> PermissionCodes.implies(p, code));
                boolean excluded = excludes.stream().anyMatch(p -> PermissionCodes.implies(p, code));
                if (included && !excluded
                    && !policyGroupPermissionRepository.existsByPolicyGroupIdAndPermissionId(group.getId(), permission.getId())) {
                    PolicyGroupPermission link = PolicyGroupPermission.builder().policyGroupId(group.getId()).permissionId(permission.getId()).build();
                    IdGenerator.assignIfAbsent(link);
                    policyGroupPermissionRepository.save(link);
                    c.policyGroupLinks++;
                }
            }
        }
        return byCode;
    }

    private Map<String, Role> importRoles(App app, AppAuthorizationCatalog catalog, Map<String, PolicyGroup> groups, Counter c) {
        Map<String, Role> byCode = new HashMap<>();
        for (RoleSpec spec : catalog.rolesOrEmpty()) {
            Role role = roleRepository.findFirstByAppIdAndCodeAndOrganizationIdIsNull(app.getId(), spec.code()).orElseGet(() -> {
                Role created = Role.builder().appId(app.getId()).code(spec.code()).name(spec.name()).description(spec.description()).build();
                IdGenerator.assignIfAbsent(created);
                c.roles++;
                return roleRepository.save(created);
            });
            for (String groupCode : spec.policyGroups() != null ? spec.policyGroups() : List.<String>of()) {
                PolicyGroup group = groups.get(groupCode);
                if (!rolePolicyGroupRepository.existsByRoleIdAndPolicyGroupId(role.getId(), group.getId())) {
                    RolePolicyGroup link = RolePolicyGroup.builder().roleId(role.getId()).policyGroupId(group.getId()).build();
                    IdGenerator.assignIfAbsent(link);
                    rolePolicyGroupRepository.save(link);
                }
            }
            byCode.put(spec.code(), role);
        }
        return byCode;
    }

    private void importUsers(App app, AppAuthorizationCatalog catalog, Map<String, Organization> orgs,
                             Map<String, Role> roles, Map<String, PolicyGroup> groups, Counter c) {
        for (UserSpec spec : catalog.usersOrEmpty()) {
            String email = spec.email().trim();
            Organization org = spec.organizationCode() != null ? orgs.get(spec.organizationCode()) : null;

            User user = userRepository.findByEmailAndAppId(email, app.getId()).orElseGet(() -> {
                User created = User.builder()
                    .appId(app.getId())
                    .email(email)
                    .password(passwordEncoder.encode(spec.password()))
                    .firstName(spec.firstName())
                    .lastName(spec.lastName())
                    .mobile(spec.mobile())
                    .birthDate(spec.birthDate() != null ? spec.birthDate() : "1970-01-01")
                    .build();
                IdGenerator.assignIfAbsent(created);
                c.users++;
                return userRepository.save(created);
            });
            if (!applicationUserRepository.existsByAppIdAndUserId(app.getId(), user.getId())) {
                ApplicationUser link = ApplicationUser.builder().appId(app.getId()).userId(user.getId()).build();
                IdGenerator.assignIfAbsent(link);
                applicationUserRepository.save(link);
            }

            UserProfile profile = profileRepository.findByUserIdAndAppId(user.getId(), app.getId()).orElseGet(() -> {
                UserProfile created = UserProfile.builder()
                    .userId(user.getId())
                    .appId(app.getId())
                    .profileSlug(spec.profileSlug() != null ? spec.profileSlug() : ProfileSlugHelper.defaultSlug(null))
                    .profileName(spec.profileName())
                    .tenancy(org != null ? ProfileTenancyEnum.ORGANIZATION : parseTenancy(spec.tenancy()))
                    .organizationId(org != null ? org.getId() : null)
                    .registrationStatus(parseStatus(spec.registrationStatus()))
                    .registeredAt(Instant.now())
                    .build();
                IdGenerator.assignIfAbsent(created);
                return profileRepository.save(created);
            });
            if (org != null && profile.getOrganizationId() == null) {
                profile.setOrganizationId(org.getId());
                profile.setTenancy(ProfileTenancyEnum.ORGANIZATION);
                profile = profileRepository.save(profile);
            }
            if (org != null && !organizationUserRepository.existsByUserIdAndOrgId(user.getId(), org.getId())) {
                OrganizationUser link = OrganizationUser.builder().userId(user.getId()).orgId(org.getId()).build();
                IdGenerator.assignIfAbsent(link);
                organizationUserRepository.save(link);
            }

            UUID profileId = profile.getId();
            for (String roleCode : spec.roles() != null ? spec.roles() : List.<String>of()) {
                Role role = roles.get(roleCode);
                if (!profileRoleRepository.existsByProfileIdAndRoleId(profileId, role.getId())) {
                    ProfileRole link = ProfileRole.builder().profileId(profileId).roleId(role.getId()).grantedAt(Instant.now()).build();
                    IdGenerator.assignIfAbsent(link);
                    profileRoleRepository.save(link);
                    c.userGrants++;
                }
            }
            for (String groupCode : spec.policyGroups() != null ? spec.policyGroups() : List.<String>of()) {
                PolicyGroup group = groups.get(groupCode);
                if (!profilePolicyGroupRepository.existsByProfileIdAndPolicyGroupId(profileId, group.getId())) {
                    ProfilePolicyGroup link = ProfilePolicyGroup.builder().profileId(profileId).policyGroupId(group.getId()).grantedAt(Instant.now()).build();
                    IdGenerator.assignIfAbsent(link);
                    profilePolicyGroupRepository.save(link);
                    c.userGrants++;
                }
            }
            for (PermissionOverrideSpec override : spec.permissionOverrides() != null ? spec.permissionOverrides() : List.<PermissionOverrideSpec>of()) {
                Permission permission = permissionRepository.findFirstByCode(override.code()).orElseThrow();
                PermissionEffectEnum effect = PermissionEffectEnum.valueOf(override.effect());
                Optional<ProfilePermission> existing = profilePermissionRepository.findByProfileIdAndIsActiveTrue(profileId).stream()
                    .filter(pp -> permission.getId().equals(pp.getPermissionId()))
                    .findFirst();
                if (existing.isEmpty() && !profilePermissionRepository.existsByProfileIdAndPermissionId(profileId, permission.getId())) {
                    ProfilePermission link = ProfilePermission.builder().profileId(profileId).permissionId(permission.getId()).effect(effect).build();
                    IdGenerator.assignIfAbsent(link);
                    profilePermissionRepository.save(link);
                    c.userGrants++;
                } else if (existing.isPresent() && existing.get().getEffect() != effect) {
                    existing.get().setEffect(effect);
                    profilePermissionRepository.save(existing.get());
                    c.userGrants++;
                }
            }
        }
    }

    private static ProfileTenancyEnum parseTenancy(String value) {
        return value != null ? ProfileTenancyEnum.valueOf(value) : ProfileTenancyEnum.PERSONAL;
    }

    private static RegistrationStatusEnum parseStatus(String value) {
        return value != null ? RegistrationStatusEnum.valueOf(value) : RegistrationStatusEnum.Created;
    }

    private static String capitalize(String s) {
        return s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private static final class Counter {
        int organizations, permissions, policyGroups, policyGroupLinks, roles, users, userGrants;
    }
}
