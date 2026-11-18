package com.ayanlab.pms.backend.service;

import com.ayanlab.core.data.CoreContext;
import com.ayanlab.core.entity.Permission;
import com.ayanlab.core.entity.Role;
import com.ayanlab.pms.backend.data.requests.AssignProfileRoleRequest;
import com.ayanlab.pms.backend.data.requests.CreateAuthRoleRequest;
import com.ayanlab.pms.backend.data.requests.UpdateMyProfileRequest;
import com.ayanlab.pms.backend.data.requests.UpdateOrgMemberRequest;
import com.ayanlab.pms.backend.data.responses.AuthPermissionResponse;
import com.ayanlab.pms.backend.data.responses.AuthRoleResponse;
import com.ayanlab.pms.backend.data.responses.MyProfileResponse;
import com.ayanlab.pms.backend.data.responses.OrgMemberResponse;

import java.util.List;
import java.util.UUID;

public interface IIpmsAuthorizationAdminService {

    List<OrgMemberResponse> listOrgMembers(CoreContext context);

    OrgMemberResponse updateOrgMember(UUID profileId, UpdateOrgMemberRequest request, CoreContext context);

    OrgMemberResponse assignRole(UUID profileId, AssignProfileRoleRequest request, CoreContext context);

    List<AuthRoleResponse> listRoles(CoreContext context);

    AuthRoleResponse createRole(CreateAuthRoleRequest request, CoreContext context);

    List<AuthPermissionResponse> listPermissions(CoreContext context);

    List<AuthPermissionResponse> listRolePermissions(UUID roleId, CoreContext context);

    MyProfileResponse getMyProfile(CoreContext context);

    MyProfileResponse updateMyProfile(UpdateMyProfileRequest request, CoreContext context);

    Role requireRole(UUID roleId, CoreContext context);

    Permission requirePermission(UUID permissionId, CoreContext context);
}
