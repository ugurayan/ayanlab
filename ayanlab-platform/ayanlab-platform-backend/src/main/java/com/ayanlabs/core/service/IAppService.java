package com.ayanlabs.core.service;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.responses.PagedResponse;
import com.ayanlabs.core.entity.App;

import java.util.UUID;

public interface IAppService extends IBaseService<App> {
    PagedResponse<App> findAll(int page, int size, CoreContext context);

    PagedResponse<App> findAll(int page, int size, UUID appId, String code, String name, CoreContext context);
}
