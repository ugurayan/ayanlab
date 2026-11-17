package com.ayanlabs.core.service;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.entity.ApplicationOrganization;

import java.util.List;
import java.util.UUID;

public interface IApplicationOrganizationService extends IBaseService<ApplicationOrganization> {

    List<ApplicationOrganization> list(UUID appId, boolean includeInactive, CoreContext context);
}
