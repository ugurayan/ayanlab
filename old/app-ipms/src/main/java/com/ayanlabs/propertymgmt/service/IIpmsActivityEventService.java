package com.ayanlabs.propertymgmt.service;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.propertymgmt.entity.platform.IpmsActivityEvent;

import java.util.List;
import java.util.UUID;

public interface IIpmsActivityEventService {

    List<IpmsActivityEvent> findRecent(CoreContext context, Integer limit);

    IpmsActivityEvent findById(UUID id, CoreContext context);

    IpmsActivityEvent create(IpmsActivityEvent item, CoreContext context);

    IpmsActivityEvent update(IpmsActivityEvent item, CoreContext context);

    void deleteById(UUID id, CoreContext context);
}
