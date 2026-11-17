package com.ayanlabs.core.service;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.entity.ProfilePolicyGroup;

import java.util.List;
import java.util.UUID;

public interface IProfilePolicyGroupService extends IBaseService<ProfilePolicyGroup> {

    List<ProfilePolicyGroup> findByProfileId(UUID profileId, CoreContext context);
}
