package com.ayanlab.core.service;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.dto.response.PagedResponse;
import com.ayanlab.core.entity.Module;

import java.util.UUID;

public interface IModuleService extends IBaseService<Module> {
    PagedResponse<Module> findAll(int page, int size, CoreContext context);

    PagedResponse<Module> findAll(int page, int size, UUID appId, String code, String name, CoreContext context);
}
