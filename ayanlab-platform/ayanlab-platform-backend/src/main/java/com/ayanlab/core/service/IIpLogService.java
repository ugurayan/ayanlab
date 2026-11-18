package com.ayanlab.core.service;

import com.ayanlab.core.data.CoreContext;
import com.ayanlab.core.entity.IpLog;

import java.util.List;
import java.util.UUID;

public interface IIpLogService {

    void record(String action, boolean success, CoreContext context);

    List<IpLog> findByUserId(UUID userId, CoreContext context);

    List<IpLog> findByIpAddress(String ipAddress, CoreContext context);
}