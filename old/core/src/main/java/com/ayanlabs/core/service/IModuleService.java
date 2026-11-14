package com.ayanlabs.core.service;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.responses.PagedResponse;
import com.ayanlabs.core.entity.Module;

import java.util.UUID;

public interface IModuleService extends IBaseService<Module> {
    PagedResponse<Module> findAll(int page, int size, CoreContext context);

    PagedResponse<Module> findAll(int page, int size, UUID appId, String code, String name, CoreContext context);
}
