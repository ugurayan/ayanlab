package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.hrm.backend.entity.JobTemplate;
import com.ayanlab.hrm.backend.repository.JobTemplateRepository;
import com.ayanlab.hrm.backend.service.JobTemplateService;
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
public class JobTemplateServiceImpl implements JobTemplateService {

    private static final String PERM_READ = "staffiq:job_templates:read";
    private static final String PERM_CREATE = "staffiq:job_templates:create";
    private static final String PERM_UPDATE = "staffiq:job_templates:update";
    private static final String PERM_DELETE = "staffiq:job_templates:delete";

    private final JobTemplateRepository repository;
    private final HrmPlatformAuthorizationService platformAuthorization;


    @Override
    public List<JobTemplate> findAll(CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        return repository.findAll();
    }

    @Override
    public JobTemplate findById(String id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        JobTemplate row = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "JobTemplate not found"));
        HrmProfileContextSupport.assertReadable(row, context);
        return row;
    }

    @Override
    @Transactional
    public JobTemplate create(JobTemplate body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_CREATE);
        if (body.getId() == null || body.getId().isBlank()) {
            body.setId(UUID.randomUUID().toString());
        }
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public JobTemplate update(JobTemplate body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_UPDATE);
        JobTemplate existing = repository
                .findById(body.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "JobTemplate not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public void deleteById(String id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_DELETE);
        JobTemplate existing = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "JobTemplate not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        repository.deleteById(id);
    }
}
