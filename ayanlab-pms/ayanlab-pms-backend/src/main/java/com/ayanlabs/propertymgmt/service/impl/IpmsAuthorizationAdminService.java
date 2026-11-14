package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.entity.*;
import com.ayanlabs.core.exception.CoreException;
import com.ayanlabs.core.service.*;
import com.ayanlabs.propertymgmt.data.requests.AssignProfileRoleRequest;
import com.ayanlabs.propertymgmt.data.requests.CreateAuthRoleRequest;
import com.ayanlabs.propertymgmt.data.requests.UpdateMyProfileRequest;
import com.ayanlabs.propertymgmt.data.requests.UpdateOrgMemberRequest;
import com.ayanlabs.propertymgmt.data.responses.AuthPermissionResponse;
import com.ayanlabs.propertymgmt.data.responses.AuthRoleResponse;
import com.ayanlabs.propertymgmt.data.responses.MyProfileResponse;
import com.ayanlabs.propertymgmt.data.responses.OrgMemberResponse;
import com.ayanlabs.propertymgmt.service.IIpmsAuthorizationAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IpmsAuthorizationAdminService implements IIpmsAuthorizationAdminService {

    private final IProfileService profileService;
    private final IUserService userService;
    private final IRoleService roleService;
    private final IPermissionService permissionService;
    private final IProfileRoleService profileRoleService;
    private final IRolePermissionService rolePermissionService;
    private final IApplicationRoleService applicationRoleService;

    @Override
    @Transactional(readOnly = true)
    public List<OrgMemberResponse> listOrgMembers(CoreContext context) {
        UUID orgId = requireOrg(context);
        return profileService.findByOrganization(orgId, context.getAppId(), context).stream()
                .map(p -> toMember(p, context))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrgMemberResponse updateOrgMember(UUID profileId, UpdateOrgMemberRequest request, CoreContext context) {
        UserProfile profile = requireOrgProfile(profileId, context);
        User user = requireUser(profile.getUserId(), context);
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        if (request.email() != null && !request.email().isBlank()) {
            user.setEmail(request.email().trim());
        }
        if (request.phone() != null) {
            user.setMobile(request.phone().trim().isEmpty() ? null : request.phone().trim());
        }
        userService.update(user, context);
        if (request.roleId() != null) {
            replacePrimaryRole(profile.getId(), request.roleId(), context);
        }
        return toMember(profileService.findById(profileId, context), context);
    }

    @Override
    @Transactional
    public OrgMemberResponse assignRole(UUID profileId, AssignProfileRoleRequest request, CoreContext context) {
        UserProfile profile = requireOrgProfile(profileId, context);
        requireRole(request.roleId(), context);
        replacePrimaryRole(profile.getId(), request.roleId(), context);
        return toMember(profile, context);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthRoleResponse> listRoles(CoreContext context) {
        return roleService.findAll(context.getAppId(), context).stream()
                .map(r -> toRole(r, context))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AuthRoleResponse createRole(CreateAuthRoleRequest request, CoreContext context) {
        UUID orgId = requireOrg(context);
        UUID appId = context.getAppId();
        if (appId == null) {
            throw new CoreException("INVALID_ARGUMENT", "appId required");
        }
        String slug = request.name().trim().toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("^_|_$", "");
        if (slug.isBlank()) {
            throw new CoreException("VALIDATION_ERROR", "Role name is invalid");
        }
        String code = "ipms.custom." + shortOrg(orgId) + "." + slug;
        Role role = Role.builder()
                .code(code)
                .name(request.name().trim())
                .description(request.description())
                .build();
        role = roleService.create(role, context);
        applicationRoleService.create(ApplicationRole.builder().appId(appId).roleId(role.getId()).build(), context);

        if (request.baseRoleId() != null) {
            List<RolePermission> basePerms = rolePermissionService.findByRoleId(request.baseRoleId(), context);
            for (RolePermission rp : basePerms) {
                try {
                    rolePermissionService.create(
                            RolePermission.builder().roleId(role.getId()).permissionId(rp.getPermissionId()).build(),
                            context
                    );
                } catch (CoreException ignored) {
                    // skip duplicates
                }
            }
        }
        return toRole(role, context);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthPermissionResponse> listPermissions(CoreContext context) {
        List<Permission> permissions = permissionService.findAll("ipms", context);
        if (permissions.isEmpty()) {
            permissions = permissionService.findAll(null, context);
        }
        return permissions.stream()
                .map(this::toPermission)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthPermissionResponse> listRolePermissions(UUID roleId, CoreContext context) {
        requireRole(roleId, context);
        List<AuthPermissionResponse> out = new ArrayList<>();
        for (RolePermission rp : rolePermissionService.findByRoleId(roleId, context)) {
            Permission p = permissionService.findById(rp.getPermissionId(), context);
            if (p != null) {
                out.add(toPermission(p));
            }
        }
        return out;
    }

    @Override
    @Transactional(readOnly = true)
    public MyProfileResponse getMyProfile(CoreContext context) {
        UUID userId = context.getUserId();
        if (userId == null) {
            throw new CoreException("UNAUTHORIZED", "Authenticated user required");
        }
        User user = requireUser(userId, context);
        UUID profileId = context.getProfileId();
        return new MyProfileResponse(
                user.getId(),
                profileId,
                context.getOrgId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getMobile(),
                Boolean.TRUE.equals(user.getIsEmailVerified()),
                Boolean.TRUE.equals(user.getIsMobileVerified()),
                Boolean.TRUE.equals(user.getFullNameVerified()),
                user.getAvatarUrl()
        );
    }

    @Override
    @Transactional
    public MyProfileResponse updateMyProfile(UpdateMyProfileRequest request, CoreContext context) {
        UUID userId = context.getUserId();
        if (userId == null) {
            throw new CoreException("UNAUTHORIZED", "Authenticated user required");
        }
        User user = requireUser(userId, context);
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        if (request.email() != null && !request.email().isBlank()) {
            user.setEmail(request.email().trim());
        }
        if (request.phone() != null) {
            user.setMobile(request.phone().trim().isEmpty() ? null : request.phone().trim());
        }
        userService.update(user, context);
        return getMyProfile(context);
    }

    @Override
    public Role requireRole(UUID roleId, CoreContext context) {
        Role role = roleService.findById(roleId, context);
        if (role == null) {
            throw new CoreException("NOT_FOUND", "Role not found");
        }
        return role;
    }

    @Override
    public Permission requirePermission(UUID permissionId, CoreContext context) {
        Permission permission = permissionService.findById(permissionId, context);
        if (permission == null) {
            throw new CoreException("NOT_FOUND", "Permission not found");
        }
        return permission;
    }

    private void replacePrimaryRole(UUID profileId, UUID roleId, CoreContext context) {
        List<ProfileRole> existing = profileRoleService.findByProfileId(profileId, context);
        for (ProfileRole pr : existing) {
            profileRoleService.deleteById(pr.getId(), context);
        }
        profileRoleService.create(
                ProfileRole.builder().profileId(profileId).roleId(roleId).build(),
                context
        );
    }

    private OrgMemberResponse toMember(UserProfile profile, CoreContext context) {
        User user = userService.findById(profile.getUserId(), context);
        List<ProfileRole> profileRoles = profileRoleService.findByProfileId(profile.getId(), context);
        List<String> codes = new ArrayList<>();
        UUID primaryRoleId = null;
        String primaryCode = null;
        String primaryName = null;
        for (ProfileRole pr : profileRoles) {
            Role role = roleService.findById(pr.getRoleId(), context);
            if (role == null) continue;
            codes.add(role.getCode());
            if (primaryRoleId == null) {
                primaryRoleId = role.getId();
                primaryCode = role.getCode();
                primaryName = role.getName();
            }
        }
        String first = user != null ? user.getFirstName() : "";
        String last = user != null ? user.getLastName() : "";
        String name = (first + " " + last).trim();
        return new OrgMemberResponse(
                profile.getId(),
                profile.getUserId(),
                profile.getOrganizationId(),
                first,
                last,
                name.isBlank() ? (user != null ? user.getEmail() : profile.getId().toString()) : name,
                user != null ? user.getEmail() : null,
                user != null ? user.getMobile() : null,
                user != null && Boolean.TRUE.equals(user.getIsEmailVerified()),
                user != null && Boolean.TRUE.equals(user.getIsMobileVerified()),
                primaryRoleId,
                primaryCode,
                primaryName,
                codes
        );
    }

    private AuthRoleResponse toRole(Role role, CoreContext context) {
        List<AuthPermissionResponse> perms = listRolePermissions(role.getId(), context);
        boolean custom = role.getCode() != null && role.getCode().startsWith("ipms.custom.");
        return new AuthRoleResponse(
                role.getId(),
                role.getCode(),
                role.getName(),
                role.getDescription(),
                custom,
                perms.size(),
                perms.stream().map(AuthPermissionResponse::code).collect(Collectors.toList())
        );
    }

    private AuthPermissionResponse toPermission(Permission p) {
        return new AuthPermissionResponse(
                p.getId(),
                p.getCode(),
                p.getName(),
                p.getDescription(),
                p.getModule(),
                p.getResource(),
                p.getAction()
        );
    }

    private UserProfile requireOrgProfile(UUID profileId, CoreContext context) {
        UUID orgId = requireOrg(context);
        UserProfile profile = profileService.findById(profileId, context);
        if (profile == null) {
            throw new CoreException("NOT_FOUND", "Profile not found");
        }
        if (profile.getOrganizationId() == null || !orgId.equals(profile.getOrganizationId())) {
            throw new CoreException("FORBIDDEN", "Profile is outside your organization");
        }
        return profile;
    }

    private User requireUser(UUID userId, CoreContext context) {
        User user = userService.findById(userId, context);
        if (user == null) {
            throw new CoreException("NOT_FOUND", "User not found");
        }
        return user;
    }

    private UUID requireOrg(CoreContext context) {
        UUID orgId = context.getOrgId();
        if (orgId == null) {
            throw new CoreException("FORBIDDEN", "orgId required in context");
        }
        return orgId;
    }

    private static String shortOrg(UUID orgId) {
        return orgId.toString().replace("-", "").substring(0, 8);
    }
}
