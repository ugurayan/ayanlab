package com.ayanlabs.core.service;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.entity.RolePermission;

import java.util.List;
import java.util.UUID;

public interface IRolePermissionService extends IBaseService<RolePermission> {

    List<RolePermission> findByRoleId(UUID roleId, CoreContext context);
}

