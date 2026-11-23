package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.hrm.backend.entity.JobRoleCategory;
import com.ayanlab.hrm.backend.repository.JobRoleCategoryRepository;
import com.ayanlab.hrm.backend.service.JobRoleCategoryService;
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
public class JobRoleCategoryServiceImpl implements JobRoleCategoryService {

    private static final String PERM_READ = "staffiq:industries:read";
    private static final String PERM_CREATE = "staffiq:industries:create";
    private static final String PERM_UPDATE = "staffiq:industries:update";
    private static final String PERM_DELETE = "staffiq:industries:delete";

    private final JobRoleCategoryRepository repository;
    private final HrmPlatformAuthorizationService platformAuthorization;


    @Override
    public List<JobRoleCategory> findAll(CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        return repository.findAll();
    }

    @Override
    public JobRoleCategory findById(String id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        JobRoleCategory row = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "JobRoleCategory not found"));
        HrmProfileContextSupport.assertReadable(row, context);
        return row;
    }

    @Override
    @Transactional
    public JobRoleCategory create(JobRoleCategory body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_CREATE);
        if (body.getId() == null || body.getId().isBlank()) {
            body.setId(UUID.randomUUID().toString());
        }
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public JobRoleCategory update(JobRoleCategory body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_UPDATE);
        JobRoleCategory existing = repository
                .findById(body.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "JobRoleCategory not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public void deleteById(String id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_DELETE);
        JobRoleCategory existing = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "JobRoleCategory not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        repository.deleteById(id);
    }
}
