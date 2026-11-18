package com.ayanlab.core.service;

import com.ayanlab.core.data.CoreContext;
import com.ayanlab.core.entity.ApplicationModule;

import java.util.List;
import java.util.UUID;

public interface IApplicationModuleService extends IBaseService<ApplicationModule> {

    List<ApplicationModule> list(UUID appId, boolean includeInactive, CoreContext context);
}
