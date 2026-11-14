package com.ayanlabs.core.service;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.responses.PagedResponse;
import com.ayanlabs.core.entity.UserProfile;

import java.util.List;
import java.util.UUID;

public interface IProfileService extends IBaseService<UserProfile> {

    List<UserProfile> findByApp(UUID appId, CoreContext context);

    List<UserProfile> findByUser(UUID userId, CoreContext context);

    List<UserProfile> findByOrganization(UUID organizationId, UUID appId, CoreContext context);
}

