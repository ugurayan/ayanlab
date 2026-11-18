package com.ayanlab.core.service;

import com.ayanlab.core.data.CoreContext;
import com.ayanlab.core.entity.LoginHistory;

import java.util.List;
import java.util.UUID;

public interface ILoginHistoryService {

    void record(UUID userId, UUID appId, UUID profileId, boolean success, String failureReason, CoreContext context);

    List<LoginHistory> findByUserId(UUID userId, CoreContext context);
}