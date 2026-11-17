package com.ayanlabs.core.service;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.entity.ProfilePermission;

import java.util.List;
import java.util.UUID;

public interface IProfilePermissionService extends IBaseService<ProfilePermission> {

    List<ProfilePermission> findByProfileId(UUID profileId, CoreContext context);
}
