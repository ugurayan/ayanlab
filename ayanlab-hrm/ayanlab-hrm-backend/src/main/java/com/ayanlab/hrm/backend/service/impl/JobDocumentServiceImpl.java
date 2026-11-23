package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.hrm.backend.entity.JobDocument;
import com.ayanlab.hrm.backend.repository.JobDocumentRepository;
import com.ayanlab.hrm.backend.service.JobDocumentService;
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
public class JobDocumentServiceImpl implements JobDocumentService {

    private static final String PERM_READ = "staffiq:job_documents:read";
    private static final String PERM_CREATE = "staffiq:job_documents:create";
    private static final String PERM_UPDATE = "staffiq:job_documents:update";
    private static final String PERM_DELETE = "staffiq:job_documents:delete";

    private final JobDocumentRepository repository;
    private final HrmPlatformAuthorizationService platformAuthorization;


    @Override
    public List<JobDocument> findAll(CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        return repository.findAll();
    }

    @Override
    public JobDocument findById(String id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        JobDocument row = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "JobDocument not found"));
        HrmProfileContextSupport.assertReadable(row, context);
        return row;
    }

    @Override
    @Transactional
    public JobDocument create(JobDocument body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_CREATE);
        if (body.getId() == null || body.getId().isBlank()) {
            body.setId(UUID.randomUUID().toString());
        }
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public JobDocument update(JobDocument body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_UPDATE);
        JobDocument existing = repository
                .findById(body.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "JobDocument not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public void deleteById(String id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_DELETE);
        JobDocument existing = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "JobDocument not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        repository.deleteById(id);
    }
}
