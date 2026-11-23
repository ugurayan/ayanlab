package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.hrm.backend.entity.FinanceRecord;
import com.ayanlab.hrm.backend.repository.FinanceRecordRepository;
import com.ayanlab.hrm.backend.service.FinanceRecordService;
import com.ayanlab.hrm.backend.service.platform.HrmPlatformAuthorizationService;
import com.ayanlab.hrm.backend.service.support.HrmProfileContextSupport;
import com.ayanlab.core.data.CoreContext;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


@Service
@RequiredArgsConstructor
public class FinanceRecordServiceImpl implements FinanceRecordService {

    private static final String PERM_READ = "staffiq:finance:read";
    private static final String PERM_CREATE = "staffiq:finance:create";
    private static final String PERM_UPDATE = "staffiq:finance:update";
    private static final String PERM_DELETE = "staffiq:finance:delete";

    private final FinanceRecordRepository repository;
    private final HrmPlatformAuthorizationService platformAuthorization;


    @Override
    public List<FinanceRecord> findAll(CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        return repository.findAll();
    }

    @Override
    public FinanceRecord findById(String id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        FinanceRecord row = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "FinanceRecord not found"));
        HrmProfileContextSupport.assertReadable(row, context);
        return row;
    }

    @Override
    @Transactional
    public FinanceRecord create(FinanceRecord body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_CREATE);
        if (body.getId() == null || body.getId().isBlank()) {
            body.setId(UUID.randomUUID().toString());
        }
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public FinanceRecord update(FinanceRecord body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_UPDATE);
        FinanceRecord existing = repository
                .findById(body.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "FinanceRecord not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public void deleteById(String id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_DELETE);
        FinanceRecord existing = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "FinanceRecord not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        repository.deleteById(id);
    }
}
