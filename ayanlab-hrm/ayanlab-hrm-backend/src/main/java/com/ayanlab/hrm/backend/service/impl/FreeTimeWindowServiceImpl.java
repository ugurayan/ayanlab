package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.hrm.backend.entity.FreeTimeWindow;
import com.ayanlab.hrm.backend.repository.FreeTimeWindowRepository;
import com.ayanlab.hrm.backend.service.FreeTimeWindowService;
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
public class FreeTimeWindowServiceImpl implements FreeTimeWindowService {

    private static final String PERM_READ = "staffiq:free_time_windows:read";
    private static final String PERM_CREATE = "staffiq:free_time_windows:create";
    private static final String PERM_UPDATE = "staffiq:free_time_windows:update";
    private static final String PERM_DELETE = "staffiq:free_time_windows:delete";

    private final FreeTimeWindowRepository repository;
    private final HrmPlatformAuthorizationService platformAuthorization;


    @Override
    public List<FreeTimeWindow> findAll(CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        return repository.findAll();
    }

    @Override
    public FreeTimeWindow findById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        FreeTimeWindow row = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "FreeTimeWindow not found"));
        HrmProfileContextSupport.assertReadable(row, context);
        return row;
    }

    @Override
    @Transactional
    public FreeTimeWindow create(FreeTimeWindow body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_CREATE);
        if (body.getId() == null) {
            body.setId(UUID.randomUUID());
        }
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public FreeTimeWindow update(FreeTimeWindow body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_UPDATE);
        FreeTimeWindow existing = repository
                .findById(body.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "FreeTimeWindow not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_DELETE);
        FreeTimeWindow existing = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "FreeTimeWindow not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        repository.deleteById(id);
    }
}
