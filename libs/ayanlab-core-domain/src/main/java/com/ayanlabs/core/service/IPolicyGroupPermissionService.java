package com.ayanlabs.core.service;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.entity.PolicyGroupPermission;

import java.util.List;
import java.util.UUID;

public interface IPolicyGroupPermissionService extends IBaseService<PolicyGroupPermission> {

    List<PolicyGroupPermission> findByPolicyGroupId(UUID policyGroupId, CoreContext context);
}
