package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.hrm.backend.entity.DocumentVerification;
import com.ayanlab.hrm.backend.repository.DocumentVerificationRepository;
import com.ayanlab.hrm.backend.service.DocumentVerificationService;
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
public class DocumentVerificationServiceImpl implements DocumentVerificationService {

    private static final String PERM_READ = "staffiq:verification:read";
    private static final String PERM_CREATE = "staffiq:verification:create";
    private static final String PERM_UPDATE = "staffiq:verification:update";
    private static final String PERM_DELETE = "staffiq:verification:delete";

    private final DocumentVerificationRepository repository;
    private final HrmPlatformAuthorizationService platformAuthorization;


    @Override
    public List<DocumentVerification> findAll(CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        if (context.getOrgId() != null) {
            return repository.findByOrgId(context.getOrgId().toString());
        }
        if (context.getProfileId() != null) {
            return repository.findByWorkerId(context.getProfileId().toString());
        }
        return repository.findAll();
    }

    @Override
    public DocumentVerification findById(String id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        DocumentVerification row = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DocumentVerification not found"));
        HrmProfileContextSupport.assertReadable(row, context);
        return row;
    }

    @Override
    @Transactional
    public DocumentVerification create(DocumentVerification body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_CREATE);
        if (body.getId() == null || body.getId().isBlank()) {
            body.setId(UUID.randomUUID().toString());
        }
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public DocumentVerification update(DocumentVerification body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_UPDATE);
        DocumentVerification existing = repository
                .findById(body.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DocumentVerification not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public void deleteById(String id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_DELETE);
        DocumentVerification existing = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DocumentVerification not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        repository.deleteById(id);
    }
}
