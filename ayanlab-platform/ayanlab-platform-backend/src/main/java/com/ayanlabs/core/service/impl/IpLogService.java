package com.ayanlabs.core.service.impl;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.enums.ActionEnum;
import com.ayanlabs.core.entity.IpLog;
import com.ayanlabs.core.repository.IpLogRepository;
import com.ayanlabs.core.service.IIpLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.ayanlabs.core.data.mapper.BaseMapper.map;

@Service
@RequiredArgsConstructor
public class IpLogService implements IIpLogService {

    private final IpLogRepository ipLogRepository;

    @Override
    @Transactional
    public void record(String action, boolean success, CoreContext context) {
        String ip = context != null ? context.getClientIp() : null;
        if (ip == null || ip.isBlank()) return;
        IpLog row = IpLog.builder()
                .ipAddress(ip)
                .userId(context.getUserId())
                .appId(context.getAppId())
                .action(action)
                .success(success)
                .loggedAt(Instant.now())
                .build();
        map(row, context.getUserId(), ActionEnum.CREATE);
        ipLogRepository.save(row);
    }

    @Override
    public List<IpLog> findByUserId(UUID userId, CoreContext context) {
        return ipLogRepository.findByUserIdOrderByLoggedAtDesc(userId);
    }

    @Override
    public List<IpLog> findByIpAddress(String ipAddress, CoreContext context) {
        return ipLogRepository.findByIpAddressOrderByLoggedAtDesc(ipAddress);
    }
}