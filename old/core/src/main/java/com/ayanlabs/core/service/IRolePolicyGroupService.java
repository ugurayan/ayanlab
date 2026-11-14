package com.ayanlabs.core.service;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.entity.RolePolicyGroup;

import java.util.List;
import java.util.UUID;

public interface IRolePolicyGroupService extends IBaseService<RolePolicyGroup> {

    List<RolePolicyGroup> findByRoleId(UUID roleId, CoreContext context);
}
