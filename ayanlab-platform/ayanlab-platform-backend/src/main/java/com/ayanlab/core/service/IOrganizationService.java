package com.ayanlab.core.service;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.dto.response.PagedResponse;
import com.ayanlab.core.entity.Organization;

import java.util.UUID;

public interface IOrganizationService extends IBaseService<Organization> {
    PagedResponse<Organization> findAll(int page, int size, CoreContext context);

    PagedResponse<Organization> findAll(int page, int size, UUID appId, String code, String name, CoreContext context);
}
