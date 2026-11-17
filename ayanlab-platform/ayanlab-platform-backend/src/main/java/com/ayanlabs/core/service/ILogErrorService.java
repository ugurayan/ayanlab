package com.ayanlabs.core.service;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.entity.LogError;

import java.util.List;
import java.util.UUID;

public interface ILogErrorService extends IBaseService<LogError> {

    LogError logFailure(String message, String errorCode, CoreContext context);

    List<LogError> findRecent(String status, CoreContext context);
}