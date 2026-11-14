package com.ayanlabs.core.service;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.entity.Permission;

import java.util.List;

public interface IPermissionService extends IBaseService<Permission> {

    List<Permission> findAll(String appCode, CoreContext context);
}

