package com.ayanlabs.core.service;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.entity.ProfileResourceScope;

import java.util.List;
import java.util.UUID;

public interface IProfileResourceScopeService extends IBaseService<ProfileResourceScope> {

    List<ProfileResourceScope> findByProfile(UUID profileId, CoreContext context);
}
