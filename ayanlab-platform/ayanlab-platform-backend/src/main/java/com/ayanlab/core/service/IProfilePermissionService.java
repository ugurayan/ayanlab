package com.ayanlab.core.service;

import com.ayanlab.core.data.CoreContext;
import com.ayanlab.core.entity.ProfilePermission;

import java.util.List;
import java.util.UUID;

public interface IProfilePermissionService extends IBaseService<ProfilePermission> {

    List<ProfilePermission> findByProfileId(UUID profileId, CoreContext context);
}
