package com.ayanlab.core.service;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.dto.response.PagedResponse;
import com.ayanlab.core.entity.UserProfile;

import java.util.List;
import java.util.UUID;

public interface IProfileService extends IBaseService<UserProfile> {

    List<UserProfile> findByApp(UUID appId, CoreContext context);

    List<UserProfile> findByUser(UUID userId, CoreContext context);

    List<UserProfile> findByOrganization(UUID organizationId, UUID appId, CoreContext context);
}

