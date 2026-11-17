package com.ayanlabs.core.service;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.entity.ApplicationRole;

import java.util.List;
import java.util.UUID;

public interface IApplicationRoleService extends IBaseService<ApplicationRole> {

    List<ApplicationRole> findByAppId(UUID appId, CoreContext context);

    List<ApplicationRole> list(UUID appId, boolean includeInactive, CoreContext context);
}
