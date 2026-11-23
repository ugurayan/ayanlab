package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.hrm.backend.entity.SystemAudit;
import com.ayanlab.hrm.backend.repository.SystemAuditRepository;
import com.ayanlab.hrm.backend.service.SystemAuditService;
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
public class SystemAuditServiceImpl implements SystemAuditService {

    private static final String PERM_READ = "staffiq:audit:read";
    private static final String PERM_CREATE = "staffiq:audit:create";
    private static final String PERM_UPDATE = "staffiq:audit:update";
    private static final String PERM_DELETE = "staffiq:audit:delete";

    private final SystemAuditRepository repository;
    private final HrmPlatformAuthorizationService platformAuthorization;


    @Override
    public List<SystemAudit> findAll(CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        return repository.findAll();
    }

    @Override
    public SystemAudit findById(String id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        SystemAudit row = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SystemAudit not found"));
        HrmProfileContextSupport.assertReadable(row, context);
        return row;
    }

    @Override
    @Transactional
    public SystemAudit create(SystemAudit body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_CREATE);
        if (body.getId() == null || body.getId().isBlank()) {
            body.setId(UUID.randomUUID().toString());
        }
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public SystemAudit update(SystemAudit body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_UPDATE);
        SystemAudit existing = repository
                .findById(body.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SystemAudit not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public void deleteById(String id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_DELETE);
        SystemAudit existing = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SystemAudit not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        repository.deleteById(id);
    }
}
