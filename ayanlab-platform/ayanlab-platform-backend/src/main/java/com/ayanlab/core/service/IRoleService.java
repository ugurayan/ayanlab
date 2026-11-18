package com.ayanlab.core.service;

import com.ayanlab.core.data.CoreContext;
import com.ayanlab.core.entity.Role;

import java.util.List;
import java.util.UUID;

public interface IRoleService extends IBaseService<Role> {

    List<Role> findAll(UUID appId, boolean includeInactive, CoreContext context);
}

