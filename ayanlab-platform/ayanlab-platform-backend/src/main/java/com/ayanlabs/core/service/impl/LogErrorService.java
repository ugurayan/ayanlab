package com.ayanlabs.core.service.impl;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.enums.ActionEnum;
import com.ayanlabs.core.entity.LogError;
import com.ayanlabs.core.exception.CoreException;
import com.ayanlabs.core.repository.LogErrorRepository;
import com.ayanlabs.core.service.ILogErrorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.ayanlabs.core.data.mapper.BaseMapper.map;

@Service
@RequiredArgsConstructor
public class LogErrorService implements ILogErrorService {

    private final LogErrorRepository logErrorRepository;

    @Override
    @Transactional
    public LogError create(LogError item, CoreContext context) {
        item = map(item, context.getUserId(), ActionEnum.CREATE);
        if (item.getStatus() == null) item.setStatus("CREATED");
        return logErrorRepository.save(item);
    }

    @Override
    @Transactional
    public LogError update(LogError item, CoreContext context) {
        if (item.getId() == null) throw new CoreException("INVALID_ARGUMENT", "LogError id required");
        item = map(item, context.getUserId(), ActionEnum.UPDATE);
        return logErrorRepository.save(item);
    }

    @Override
    @Transactional
    public void delete(LogError item, CoreContext context) {
        item.setIsActive(false);
        item = map(item, context.getUserId(), ActionEnum.DELETE);
        logErrorRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        LogError e = findById(id, context);
        if (e != null) delete(e, context);
    }

    @Override
    public LogError findById(UUID id, CoreContext context) {
        return logErrorRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public LogError logFailure(String message, String errorCode, CoreContext context) {
        LogError row = LogError.builder()
                .status("CREATED")
                .userId(context != null ? context.getUserId() : null)
                .appId(context != null ? context.getAppId() : null)
                .message(message)
                .errorCode(errorCode)
                .ipAddress(context != null ? context.getClientIp() : null)
                .build();
        return create(row, context);
    }

    @Override
    public List<LogError> findRecent(String status, CoreContext context) {
        String st = status != null ? status : "CREATED";
        return logErrorRepository.findByStatusOrderByCreatedAtDesc(st);
    }

    @Override
    @Transactional
    public void hardDeleteById(UUID id, CoreContext context) {
        LogError found = findById(id, context);
        if (found != null) {
            logErrorRepository.delete(found);
        }
    }

    @Override
    @Transactional
    public LogError setActive(UUID id, boolean active, CoreContext context) {
        LogError found = findById(id, context);
        if (found == null) {
            throw new CoreException("NOT_FOUND", "LogError not found");
        }
        found.setIsActive(active);
        found = map(found, context.getUserId(), ActionEnum.UPDATE);
        found.setIsActive(active);
        return logErrorRepository.save(found);
    }
}