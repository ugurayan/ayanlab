package com.ayanlab.pms.backend.service;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.service.IBaseService;

import java.util.List;

public interface IIpmsOrgScopedService<T> extends IBaseService<T> {

    List<T> findAll(CoreContext context);
}
