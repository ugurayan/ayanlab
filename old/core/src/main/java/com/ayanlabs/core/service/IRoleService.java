package com.ayanlabs.core.service;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.entity.Role;

import java.util.List;
import java.util.UUID;

public interface IRoleService extends IBaseService<Role> {

    List<Role> findAll(UUID appId, CoreContext context);
}

