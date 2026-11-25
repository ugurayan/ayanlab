package com.ayanlab.core.service;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.entity.ProfilePolicyGroup;

import java.util.List;
import java.util.UUID;

public interface IProfilePolicyGroupService extends IBaseService<ProfilePolicyGroup> {

    List<ProfilePolicyGroup> findByProfileId(UUID profileId, CoreContext context);
}
