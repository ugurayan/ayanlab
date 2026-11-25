package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.hrm.backend.entity.TransactionRecord;
import com.ayanlab.hrm.backend.repository.TransactionRecordRepository;
import com.ayanlab.hrm.backend.service.TransactionRecordService;
import com.ayanlab.hrm.backend.service.platform.HrmPlatformAuthorizationService;
import com.ayanlab.hrm.backend.util.HrmProfileContextSupport;
import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


@Service
@RequiredArgsConstructor
public class TransactionRecordServiceImpl implements TransactionRecordService {

    private static final String PERM_READ = "staffiq:transactions:read";
    private static final String PERM_CREATE = "staffiq:transactions:create";
    private static final String PERM_UPDATE = "staffiq:transactions:update";
    private static final String PERM_DELETE = "staffiq:transactions:delete";

    private final TransactionRecordRepository repository;
    private final HrmPlatformAuthorizationService platformAuthorization;


    @Override
    public List<TransactionRecord> findAll(CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        return repository.findAll();
    }

    @Override
    public TransactionRecord findById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        TransactionRecord row = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TransactionRecord not found"));
        HrmProfileContextSupport.assertReadable(row, context);
        return row;
    }

    @Override
    @Transactional
    public TransactionRecord create(TransactionRecord body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_CREATE);
        if (body.getId() == null) {
            body.setId(UUID.randomUUID());
        }
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public TransactionRecord update(TransactionRecord body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_UPDATE);
        TransactionRecord existing = repository
                .findById(body.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TransactionRecord not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_DELETE);
        TransactionRecord existing = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TransactionRecord not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        repository.deleteById(id);
    }
}
