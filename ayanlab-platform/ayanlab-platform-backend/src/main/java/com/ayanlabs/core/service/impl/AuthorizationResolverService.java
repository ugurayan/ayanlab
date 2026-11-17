package com.ayanlabs.core.service.impl;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.dto.EffectiveAuthorization;
import com.ayanlabs.core.data.dto.ResourceScopeDto;
import com.ayanlabs.core.data.enums.PermissionEffectEnum;
import com.ayanlabs.core.entity.*;
import com.ayanlabs.core.exception.CoreException;
import com.ayanlabs.core.repository.*;
import com.ayanlabs.core.service.IAuthorizationResolverService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorizationResolverService implements IAuthorizationResolverService {

    private final UserProfileRepository profileRepository;
    private final ProfileRoleRepository profileRoleRepository;
    private final ProfilePolicyGroupRepository profilePolicyGroupRepository;
    private final ProfilePermissionRepository profilePermissionRepository;
    private final ProfileResourceScopeRepository profileResourceScopeRepository;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
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

        Map<UUID, Permission> permissionById = permissionRepository.findAll().stream()
            .filter(p -> Boolean.TRUE.equals(p.getIsActive()))
            .filter(p -> p.getId() != null)
            .collect(Collectors.toMap(Permission::getId, Function.identity(), (a, b) -> a));

        Set<UUID> appPermissionIds = applicationPermissionRepository.findByAppIdAndIsActiveTrue(appId).stream()
            .map(ApplicationPermission::getPermissionId)
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection(LinkedHashSet::new));

        Set<UUID> roleIds = profileRoleRepository.findByProfileIdAndIsActiveTrue(profileId).stream()
            .map(ProfileRole::getRoleId)
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection(LinkedHashSet::new));

        Set<String> roleCodes = new LinkedHashSet<>();
        Set<UUID> policyGroupIds = new LinkedHashSet<>();
        Set<UUID> grantedPermissionIds = new LinkedHashSet<>();

        for (UUID roleId : roleIds) {
            roleRepository.findById(roleId).ifPresent(role -> {
                if (Boolean.TRUE.equals(role.getIsActive())) {
                    roleCodes.add(role.getCode());
                }
            });
            rolePermissionRepository.findByRoleIdAndIsActiveTrue(roleId).stream()
                .map(RolePermission::getPermissionId)
                .forEach(grantedPermissionIds::add);
            rolePolicyGroupRepository.findByRoleIdAndIsActiveTrue(roleId).stream()
                .map(RolePolicyGroup::getPolicyGroupId)
                .forEach(policyGroupIds::add);
        }

        Instant now = Instant.now();
        profilePolicyGroupRepository.findByProfileIdAndIsActiveTrue(profileId).stream()
            .filter(ppg -> ppg.getExpiresAt() == null || ppg.getExpiresAt().isAfter(now))
            .map(ProfilePolicyGroup::getPolicyGroupId)
            .forEach(policyGroupIds::add);

        Set<String> policyGroupCodes = new LinkedHashSet<>();
        for (UUID policyGroupId : policyGroupIds) {
            policyGroupRepository.findById(policyGroupId).ifPresent(group -> {
                if (Boolean.TRUE.equals(group.getIsActive()) && appId.equals(group.getAppId())) {
                    policyGroupCodes.add(group.getCode());
                    policyGroupPermissionRepository.findByPolicyGroupIdAndIsActiveTrue(policyGroupId).stream()
                        .map(PolicyGroupPermission::getPermissionId)
                        .forEach(grantedPermissionIds::add);
                }
            });
        }

        Set<UUID> deniedPermissionIds = new LinkedHashSet<>();
        for (ProfilePermission profilePermission : profilePermissionRepository.findByProfileIdAndIsActiveTrue(profileId)) {
            if (profilePermission.getEffect() == PermissionEffectEnum.DENY) {
                deniedPermissionIds.add(profilePermission.getPermissionId());
            } else {
                grantedPermissionIds.add(profilePermission.getPermissionId());
            }
        }

        grantedPermissionIds.removeAll(deniedPermissionIds);

        Set<String> permissionCodes = grantedPermissionIds.stream()
            .filter(appPermissionIds::contains)
            .map(permissionById::get)
            .filter(Objects::nonNull)
            .map(Permission::getCode)
            .filter(Objects::nonNull)
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
}
