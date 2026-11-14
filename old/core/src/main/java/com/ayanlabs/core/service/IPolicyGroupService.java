package com.ayanlabs.core.service;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.entity.PolicyGroup;

import java.util.List;
import java.util.UUID;

public interface IPolicyGroupService extends IBaseService<PolicyGroup> {

    List<PolicyGroup> findByApp(UUID appId, CoreContext context);
}
