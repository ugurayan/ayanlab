package com.ayanlab.core.service;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.entity.ApplicationOrganization;

import java.util.List;
import java.util.UUID;

public interface IApplicationOrganizationService extends IBaseService<ApplicationOrganization> {

    List<ApplicationOrganization> list(UUID appId, boolean includeInactive, CoreContext context);
}
