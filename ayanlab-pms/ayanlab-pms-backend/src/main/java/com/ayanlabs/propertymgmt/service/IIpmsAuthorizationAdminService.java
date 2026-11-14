package com.ayanlabs.propertymgmt.service;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.entity.Permission;
import com.ayanlabs.core.entity.Role;
import com.ayanlabs.propertymgmt.data.requests.AssignProfileRoleRequest;
import com.ayanlabs.propertymgmt.data.requests.CreateAuthRoleRequest;
import com.ayanlabs.propertymgmt.data.requests.UpdateMyProfileRequest;
import com.ayanlabs.propertymgmt.data.requests.UpdateOrgMemberRequest;
import com.ayanlabs.propertymgmt.data.responses.AuthPermissionResponse;
import com.ayanlabs.propertymgmt.data.responses.AuthRoleResponse;
import com.ayanlabs.propertymgmt.data.responses.MyProfileResponse;
import com.ayanlabs.propertymgmt.data.responses.OrgMemberResponse;

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
