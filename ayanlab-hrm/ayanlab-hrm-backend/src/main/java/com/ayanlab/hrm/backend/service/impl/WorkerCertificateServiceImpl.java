package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.hrm.backend.entity.WorkerCertificate;
import com.ayanlab.hrm.backend.repository.WorkerCertificateRepository;
import com.ayanlab.hrm.backend.service.WorkerCertificateService;
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
public class WorkerCertificateServiceImpl implements WorkerCertificateService {

    private static final String PERM_READ = "staffiq:worker_certificates:read";
    private static final String PERM_CREATE = "staffiq:worker_certificates:create";
    private static final String PERM_UPDATE = "staffiq:worker_certificates:update";
    private static final String PERM_DELETE = "staffiq:worker_certificates:delete";

    private final WorkerCertificateRepository repository;
    private final HrmPlatformAuthorizationService platformAuthorization;


    @Override
    public List<WorkerCertificate> findAll(CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        if (context.getProfileId() != null) {
            return repository.findByProfileId(context.getProfileId());
        }
        if (context.getOrgId() != null) {
            return repository.findByOrgId(context.getOrgId());
        }
        return repository.findAll();
    }

    @Override
    public WorkerCertificate findById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        WorkerCertificate row = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "WorkerCertificate not found"));
        HrmProfileContextSupport.assertReadable(row, context);
        return row;
    }

    @Override
    @Transactional
    public WorkerCertificate create(WorkerCertificate body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_CREATE);
        if (body.getId() == null) {
            body.setId(UUID.randomUUID());
        }
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public WorkerCertificate update(WorkerCertificate body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_UPDATE);
        WorkerCertificate existing = repository
                .findById(body.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "WorkerCertificate not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_DELETE);
        WorkerCertificate existing = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "WorkerCertificate not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        repository.deleteById(id);
    }
}
