package com.ayanlab.core.service;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.entity.LogError;

import java.util.List;
import java.util.UUID;

public interface ILogErrorService extends IBaseService<LogError> {

    LogError logFailure(String message, String errorCode, CoreContext context);

    List<LogError> findRecent(String status, CoreContext context);
}