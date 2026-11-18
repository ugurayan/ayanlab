package com.ayanlab.core.service;

import com.ayanlab.core.data.CoreContext;
import com.ayanlab.core.entity.PolicyGroupPermission;

import java.util.List;
import java.util.UUID;

public interface IPolicyGroupPermissionService extends IBaseService<PolicyGroupPermission> {

    List<PolicyGroupPermission> findByPolicyGroupId(UUID policyGroupId, CoreContext context);
}
