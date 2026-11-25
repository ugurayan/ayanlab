package com.ayanlab.core.service;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.entity.PolicyGroup;

import java.util.List;
import java.util.UUID;

public interface IPolicyGroupService extends IBaseService<PolicyGroup> {

    List<PolicyGroup> findByApp(UUID appId, CoreContext context);
}
