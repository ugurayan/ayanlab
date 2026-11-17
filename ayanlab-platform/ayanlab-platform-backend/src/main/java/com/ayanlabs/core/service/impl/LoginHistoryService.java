package com.ayanlabs.core.service.impl;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.enums.ActionEnum;
import com.ayanlabs.core.entity.LoginHistory;
import com.ayanlabs.core.repository.LoginHistoryRepository;
import com.ayanlabs.core.service.ILoginHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.ayanlabs.core.data.mapper.BaseMapper.map;

@Service
@RequiredArgsConstructor
public class LoginHistoryService implements ILoginHistoryService {

    private final LoginHistoryRepository loginHistoryRepository;

    @Override
    @Transactional
    public void record(UUID userId, UUID appId, UUID profileId, boolean success, String failureReason, CoreContext context) {
        LoginHistory row = LoginHistory.builder()
                .userId(userId)
                .appId(appId)
                .profileId(profileId)
                .ipAddress(context != null ? context.getClientIp() : null)
                .success(success)
                .failureReason(failureReason)
                .loginAt(Instant.now())
                .build();
        map(row, userId, ActionEnum.CREATE);
        loginHistoryRepository.save(row);
    }

    @Override
    public List<LoginHistory> findByUserId(UUID userId, CoreContext context) {
        return loginHistoryRepository.findByUserIdOrderByLoginAtDesc(userId);
    }
}