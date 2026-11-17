package com.ayanlabs.core.service;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.responses.PagedResponse;
import com.ayanlabs.core.entity.Organization;

import java.util.UUID;

public interface IOrganizationService extends IBaseService<Organization> {
    PagedResponse<Organization> findAll(int page, int size, CoreContext context);

    PagedResponse<Organization> findAll(int page, int size, UUID appId, String code, String name, CoreContext context);
}
