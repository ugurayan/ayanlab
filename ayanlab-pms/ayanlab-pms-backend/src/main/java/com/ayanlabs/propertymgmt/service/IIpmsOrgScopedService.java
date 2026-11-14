package com.ayanlabs.propertymgmt.service;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.service.IBaseService;

import java.util.List;

public interface IIpmsOrgScopedService<T> extends IBaseService<T> {

    List<T> findAll(CoreContext context);
}
