package com.ayanlab.core.service;

import com.ayanlab.core.data.CoreContext;
import com.ayanlab.core.entity.ProfileResourceScope;

import java.util.List;
import java.util.UUID;

public interface IProfileResourceScopeService extends IBaseService<ProfileResourceScope> {

    List<ProfileResourceScope> findByProfile(UUID profileId, CoreContext context);
}
