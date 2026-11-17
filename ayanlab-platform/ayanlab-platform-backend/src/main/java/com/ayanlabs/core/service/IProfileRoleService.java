package com.ayanlabs.core.service;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.entity.ProfileRole;

import java.util.List;
import java.util.UUID;

public interface IProfileRoleService extends IBaseService<ProfileRole> {

    List<ProfileRole> findByProfileId(UUID profileId, CoreContext context);
}
