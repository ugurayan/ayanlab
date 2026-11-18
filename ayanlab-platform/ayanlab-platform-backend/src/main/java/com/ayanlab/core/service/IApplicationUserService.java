package com.ayanlab.core.service;

import com.ayanlab.core.data.CoreContext;
import com.ayanlab.core.entity.ApplicationUser;

import java.util.List;
import java.util.UUID;

public interface IApplicationUserService extends IBaseService<ApplicationUser> {

    List<ApplicationUser> list(UUID appId, boolean includeInactive, CoreContext context);
}
