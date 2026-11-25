package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.hrm.backend.entity.UserAudit;
import com.ayanlab.hrm.backend.repository.UserAuditRepository;
import com.ayanlab.hrm.backend.service.UserAuditService;
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
public class UserAuditServiceImpl implements UserAuditService {

    private static final String PERM_READ = "staffiq:audit:read";
    private static final String PERM_CREATE = "staffiq:audit:create";
    private static final String PERM_UPDATE = "staffiq:audit:update";
    private static final String PERM_DELETE = "staffiq:audit:delete";

    private final UserAuditRepository repository;
    private final HrmPlatformAuthorizationService platformAuthorization;


    @Override
    public List<UserAudit> findAll(CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        return repository.findAll();
    }

    @Override
    public UserAudit findById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        UserAudit row = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UserAudit not found"));
        HrmProfileContextSupport.assertReadable(row, context);
        return row;
    }

    @Override
    @Transactional
    public UserAudit create(UserAudit body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_CREATE);
        if (body.getId() == null) {
            body.setId(UUID.randomUUID());
        }
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public UserAudit update(UserAudit body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_UPDATE);
        UserAudit existing = repository
                .findById(body.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UserAudit not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_DELETE);
        UserAudit existing = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UserAudit not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        repository.deleteById(id);
    }
}
