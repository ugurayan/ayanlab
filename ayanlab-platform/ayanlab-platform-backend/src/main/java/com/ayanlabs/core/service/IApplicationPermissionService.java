package com.ayanlabs.core.service;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.entity.ApplicationPermission;

import java.util.List;
import java.util.UUID;

public interface IApplicationPermissionService extends IBaseService<ApplicationPermission> {

    List<ApplicationPermission> findByAppId(UUID appId, CoreContext context);

    List<ApplicationPermission> list(UUID appId, boolean includeInactive, CoreContext context);
}
