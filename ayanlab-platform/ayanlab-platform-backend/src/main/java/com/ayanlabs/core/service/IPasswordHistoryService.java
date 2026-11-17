package com.ayanlabs.core.service;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.entity.PasswordHistory;

import java.util.List;
import java.util.UUID;

public interface IPasswordHistoryService {

    void record(UUID userId, String passwordHash, CoreContext context);

    List<PasswordHistory> findByUserId(UUID userId, CoreContext context);
}