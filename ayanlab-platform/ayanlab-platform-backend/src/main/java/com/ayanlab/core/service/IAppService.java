package com.ayanlab.core.service;

import com.ayanlab.core.data.CoreContext;
import com.ayanlab.core.data.responses.PagedResponse;
import com.ayanlab.core.entity.App;

import java.util.UUID;

public interface IAppService extends IBaseService<App> {
    PagedResponse<App> findAll(int page, int size, CoreContext context);

    PagedResponse<App> findAll(int page, int size, UUID appId, String code, String name, CoreContext context);
}
