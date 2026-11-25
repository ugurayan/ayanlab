package com.ayanlab.core.service;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.entity.ProfileRole;

import java.util.List;
import java.util.UUID;

public interface IProfileRoleService extends IBaseService<ProfileRole> {

    List<ProfileRole> findByProfileId(UUID profileId, CoreContext context);
}
