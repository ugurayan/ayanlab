package com.ayanlabs.core.service;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.entity.ApplicationModule;

import java.util.List;
import java.util.UUID;

public interface IApplicationModuleService extends IBaseService<ApplicationModule> {

    List<ApplicationModule> list(UUID appId, boolean includeInactive, CoreContext context);
}
