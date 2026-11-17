package com.ayanlabs.core.service;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.entity.IpLog;

import java.util.List;
import java.util.UUID;

public interface IIpLogService {

    void record(String action, boolean success, CoreContext context);

    List<IpLog> findByUserId(UUID userId, CoreContext context);

    List<IpLog> findByIpAddress(String ipAddress, CoreContext context);
}