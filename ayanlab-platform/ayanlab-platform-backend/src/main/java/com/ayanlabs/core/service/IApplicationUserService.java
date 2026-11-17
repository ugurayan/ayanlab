package com.ayanlabs.core.service;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.entity.ApplicationUser;

import java.util.List;
import java.util.UUID;

public interface IApplicationUserService extends IBaseService<ApplicationUser> {

    List<ApplicationUser> list(UUID appId, boolean includeInactive, CoreContext context);
}
