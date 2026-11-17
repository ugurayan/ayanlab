package com.ayanlabs.core.service.impl;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.enums.ActionEnum;
import com.ayanlabs.core.entity.PasswordHistory;
import com.ayanlabs.core.repository.PasswordHistoryRepository;
import com.ayanlabs.core.service.IPasswordHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.ayanlabs.core.data.mapper.BaseMapper.map;

@Service
@RequiredArgsConstructor
public class PasswordHistoryService implements IPasswordHistoryService {

    private final PasswordHistoryRepository passwordHistoryRepository;

    @Override
    @Transactional
    public void record(UUID userId, String passwordHash, CoreContext context) {
        PasswordHistory row = PasswordHistory.builder()
                .userId(userId)
                .passwordHash(passwordHash)
                .changedAt(Instant.now())
                .build();
        map(row, context != null ? context.getUserId() : userId, ActionEnum.CREATE);
        passwordHistoryRepository.save(row);
    }

    @Override
    public List<PasswordHistory> findByUserId(UUID userId, CoreContext context) {
        return passwordHistoryRepository.findByUserIdOrderByChangedAtDesc(userId);
    }
}