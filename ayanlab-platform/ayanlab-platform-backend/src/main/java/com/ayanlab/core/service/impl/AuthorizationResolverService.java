package com.ayanlab.core.service.impl;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.dto.EffectiveAuthorization;
import com.ayanlab.core.dto.ResourceScopeDto;
import com.ayanlab.core.enums.PermissionEffectEnum;
import com.ayanlab.core.entity.*;
import com.ayanlab.core.exception.CoreException;
import com.ayanlab.core.repository.*;
import com.ayanlab.core.service.IAuthorizationResolverService;
import com.ayanlab.security.authz.AuthzVersionChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorizationResolverService implements IAuthorizationResolverService {

    private final UserProfileRepository profileRepository;
    private final AppRepository appRepository;
    private final ProfileRoleRepository profileRoleRepository;
    private final ProfilePolicyGroupRepository profilePolicyGroupRepository;
    private final ProfilePermissionRepository profilePermissionRepository;
    private final ProfileResourceScopeRepository profileResourceScopeRepository;
    private final RoleRepository roleRepository;
    private final RolePolicyGroupRepository rolePolicyGroupRepository;
    private final PolicyGroupRepository policyGroupRepository;
    private final PolicyGroupPermissionRepository policyGroupPermissionRepository;
    private final ApplicationPermissionRepository applicationPermissionRepository;
    private final PermissionRepository permissionRepository;

    @Override
    @Transactional(readOnly = true)
    public EffectiveAuthorization resolveForProfile(UUID profileId, CoreContext context) {
        UserProfile profile = profileRepository.findById(profileId)
            .orElseThrow(() -> new CoreException("NOT_FOUND", "Profile not found"));
        if (Boolean.FALSE.equals(profile.getIsActive())) {
            throw new CoreException("FORBIDDEN", "Profile is inactive");
        }
        UUID appId = profile.getAppId();
        if (context.getAppId() != null && !context.getAppId().equals(appId)) {
            throw new CoreException("FORBIDDEN", "Profile does not belong to current app");
        }
        Optional<App> app = appRepository.findById(appId);
        String appCode = app.map(App::getCode).orElse(null);
        Instant now = Instant.now();

        Set<UUID> assignedRoleIds = profileRoleRepository.findByProfileIdAndIsActiveTrue(profileId).stream()
            .filter(pr -> notExpired(pr.getExpiresAt(), now))
            .map(ProfileRole::getRoleId)
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection(LinkedHashSet::new));

        List<Role> roles = assignedRoleIds.isEmpty() ? List.of() : roleRepository.findAllById(assignedRoleIds).stream()
            .filter(role -> Boolean.TRUE.equals(role.getIsActive()))
            .filter(role -> appId.equals(role.getAppId()))
            .filter(role -> role.getOrganizationId() == null || role.getOrganizationId().equals(profile.getOrganizationId()))
            .toList();
        Set<UUID> roleIds = roles.stream().map(Role::getId).collect(Collectors.toCollection(LinkedHashSet::new));
        Set<String> roleCodes = roles.stream().map(Role::getCode).collect(Collectors.toCollection(LinkedHashSet::new));

        Set<UUID> grantedPermissionIds = new LinkedHashSet<>();
        Set<UUID> policyGroupIds = new LinkedHashSet<>();
        if (!roleIds.isEmpty()) {
            rolePolicyGroupRepository.findByRoleIdInAndIsActiveTrue(roleIds)
                .forEach(rpg -> policyGroupIds.add(rpg.getPolicyGroupId()));
        }
        profilePolicyGroupRepository.findByProfileIdAndIsActiveTrue(profileId).stream()
            .filter(ppg -> notExpired(ppg.getExpiresAt(), now))
            .forEach(ppg -> policyGroupIds.add(ppg.getPolicyGroupId()));

        List<PolicyGroup> groups = policyGroupIds.isEmpty() ? List.of() : policyGroupRepository.findAllById(policyGroupIds).stream()
            .filter(group -> Boolean.TRUE.equals(group.getIsActive()) && appId.equals(group.getAppId()))
            .toList();
        Set<String> policyGroupCodes = groups.stream().map(PolicyGroup::getCode).collect(Collectors.toCollection(LinkedHashSet::new));
        if (!groups.isEmpty()) {
            policyGroupPermissionRepository.findByPolicyGroupIdInAndIsActiveTrue(groups.stream().map(PolicyGroup::getId).toList())
                .forEach(pgp -> grantedPermissionIds.add(pgp.getPermissionId()));
        }

        Set<UUID> deniedPermissionIds = new HashSet<>();
        for (ProfilePermission profilePermission : profilePermissionRepository.findByProfileIdAndIsActiveTrue(profileId)) {
            if (profilePermission.getEffect() == PermissionEffectEnum.DENY) {
                deniedPermissionIds.add(profilePermission.getPermissionId());
            } else {
                grantedPermissionIds.add(profilePermission.getPermissionId());
            }
        }
        grantedPermissionIds.removeAll(deniedPermissionIds);
        grantedPermissionIds.remove(null);

        Set<UUID> appLinkedPermissionIds = applicationPermissionRepository.findByAppIdAndIsActiveTrue(appId).stream()
            .map(ApplicationPermission::getPermissionId)
            .collect(Collectors.toSet());

        Set<String> permissionCodes = grantedPermissionIds.isEmpty() ? new LinkedHashSet<>() : permissionRepository.findAllById(grantedPermissionIds).stream()
            .filter(p -> Boolean.TRUE.equals(p.getIsActive()) && p.getCode() != null)
            .filter(p -> appLinkedPermissionIds.contains(p.getId()) || (appCode != null && appCode.equals(p.getAppCode())))
            .map(Permission::getCode)
            .sorted()
            .collect(Collectors.toCollection(LinkedHashSet::new));

        List<ResourceScopeDto> resourceScopes = profileResourceScopeRepository.findByProfileIdAndIsActiveTrue(profileId).stream()
            .map(scope -> ResourceScopeDto.builder()
                .resourceType(scope.getResourceType())
                .resourceId(scope.getResourceId())
                .organizationId(scope.getOrganizationId())
                .build())
            .toList();

        return EffectiveAuthorization.builder()
            .profileId(profileId)
            .appId(appId)
            .appCode(appCode)
            .authzVersion(AuthzVersionChecker.format(app.map(App::getAuthzVersion).orElse(null), profile.getAuthzVersion()))
            .organizationId(profile.getOrganizationId())
            .roleCodes(roleCodes)
            .permissionCodes(permissionCodes)
            .policyGroupCodes(policyGroupCodes)
            .resourceScopes(resourceScopes)
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public EffectiveAuthorization resolveForCurrentProfile(CoreContext context) {
        UUID profileId = context.getProfileId();
        if (profileId == null) {
            throw new CoreException("UNAUTHORIZED", "Profile context required");
        }
        return resolveForProfile(profileId, context);
    }

    private static boolean notExpired(Instant expiresAt, Instant now) {
        return expiresAt == null || expiresAt.isAfter(now);
    }
}
