package com.ayanlab.core.service;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.entity.Permission;

import java.util.List;

public interface IPermissionService extends IBaseService<Permission> {

    List<Permission> findAll(String appCode, boolean includeInactive, CoreContext context);
}

