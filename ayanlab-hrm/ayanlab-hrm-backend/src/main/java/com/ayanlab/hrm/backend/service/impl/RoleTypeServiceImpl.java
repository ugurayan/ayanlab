package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.hrm.backend.entity.RoleType;
import com.ayanlab.hrm.backend.repository.RoleTypeRepository;
import com.ayanlab.hrm.backend.service.RoleTypeService;
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
public class RoleTypeServiceImpl implements RoleTypeService {

    private static final String PERM_READ = "staffiq:job_roles:read";
    private static final String PERM_CREATE = "staffiq:job_roles:create";
    private static final String PERM_UPDATE = "staffiq:job_roles:update";
    private static final String PERM_DELETE = "staffiq:job_roles:delete";

    private final RoleTypeRepository repository;
    private final HrmPlatformAuthorizationService platformAuthorization;


    @Override
    public List<RoleType> findAll(CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        return repository.findAll();
    }

    @Override
    public RoleType findById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        RoleType row = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "RoleType not found"));
        HrmProfileContextSupport.assertReadable(row, context);
        return row;
    }

    @Override
    @Transactional
    public RoleType create(RoleType body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_CREATE);
        if (body.getId() == null) {
            body.setId(UUID.randomUUID());
        }
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public RoleType update(RoleType body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_UPDATE);
        RoleType existing = repository
                .findById(body.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "RoleType not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_DELETE);
        RoleType existing = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "RoleType not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        repository.deleteById(id);
    }
}
