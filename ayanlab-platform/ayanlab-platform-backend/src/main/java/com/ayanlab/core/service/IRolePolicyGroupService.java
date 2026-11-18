package com.ayanlab.core.service;

import com.ayanlab.core.data.CoreContext;
import com.ayanlab.core.entity.RolePolicyGroup;

import java.util.List;
import java.util.UUID;

public interface IRolePolicyGroupService extends IBaseService<RolePolicyGroup> {

    List<RolePolicyGroup> findByRoleId(UUID roleId, CoreContext context);
}
