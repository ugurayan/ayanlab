package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.hrm.backend.entity.Job;
import com.ayanlab.hrm.backend.repository.JobRepository;
import com.ayanlab.hrm.backend.service.JobService;
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
public class JobServiceImpl implements JobService {

    private static final String PERM_READ = "staffiq:jobs:read";
    private static final String PERM_CREATE = "staffiq:jobs:create";
    private static final String PERM_UPDATE = "staffiq:jobs:update";
    private static final String PERM_DELETE = "staffiq:jobs:delete";

    private final JobRepository repository;
    private final HrmPlatformAuthorizationService platformAuthorization;


    @Override
    public List<Job> findAll(CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        return repository.findAll();
    }

    @Override
    public Job findById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        Job row = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));
        HrmProfileContextSupport.assertReadable(row, context);
        return row;
    }

    @Override
    @Transactional
    public Job create(Job body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_CREATE);
        if (body.getId() == null) {
            body.setId(UUID.randomUUID());
        }
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public Job update(Job body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_UPDATE);
        Job existing = repository
                .findById(body.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_DELETE);
        Job existing = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        repository.deleteById(id);
    }
}
