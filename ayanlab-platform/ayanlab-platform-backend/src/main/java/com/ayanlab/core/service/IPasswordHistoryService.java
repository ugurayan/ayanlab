package com.ayanlab.core.service;

import com.ayanlab.core.data.CoreContext;
import com.ayanlab.core.entity.PasswordHistory;

import java.util.List;
import java.util.UUID;

public interface IPasswordHistoryService {

    void record(UUID userId, String passwordHash, CoreContext context);

    List<PasswordHistory> findByUserId(UUID userId, CoreContext context);
}