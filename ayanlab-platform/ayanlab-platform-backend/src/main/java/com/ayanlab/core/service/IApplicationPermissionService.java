package com.ayanlab.core.service;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.entity.ApplicationPermission;

import java.util.List;
import java.util.UUID;

public interface IApplicationPermissionService extends IBaseService<ApplicationPermission> {

    List<ApplicationPermission> findByAppId(UUID appId, CoreContext context);

    List<ApplicationPermission> list(UUID appId, boolean includeInactive, CoreContext context);
}
