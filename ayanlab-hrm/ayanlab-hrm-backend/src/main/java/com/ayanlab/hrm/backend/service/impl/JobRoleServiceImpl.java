package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.hrm.backend.entity.JobRole;
import com.ayanlab.hrm.backend.repository.JobRoleRepository;
import com.ayanlab.hrm.backend.service.JobRoleService;
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
public class JobRoleServiceImpl implements JobRoleService {

    private static final String PERM_READ = "staffiq:job_roles:read";
    private static final String PERM_CREATE = "staffiq:job_roles:create";
    private static final String PERM_UPDATE = "staffiq:job_roles:update";
    private static final String PERM_DELETE = "staffiq:job_roles:delete";

    private final JobRoleRepository repository;
    private final HrmPlatformAuthorizationService platformAuthorization;


    @Override
    public List<JobRole> findAll(CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        return repository.findAll();
    }

    @Override
    public JobRole findById(String id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        JobRole row = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "JobRole not found"));
        HrmProfileContextSupport.assertReadable(row, context);
        return row;
    }

    @Override
    @Transactional
    public JobRole create(JobRole body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_CREATE);
        if (body.getId() == null || body.getId().isBlank()) {
            body.setId(UUID.randomUUID().toString());
        }
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public JobRole update(JobRole body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_UPDATE);
        JobRole existing = repository
                .findById(body.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "JobRole not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public void deleteById(String id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_DELETE);
        JobRole existing = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "JobRole not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        repository.deleteById(id);
    }
}
