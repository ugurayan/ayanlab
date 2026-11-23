package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.hrm.backend.entity.Document;
import com.ayanlab.hrm.backend.repository.DocumentRepository;
import com.ayanlab.hrm.backend.service.DocumentService;
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
public class DocumentServiceImpl implements DocumentService {

    private static final String PERM_READ = "staffiq:documents:read";
    private static final String PERM_CREATE = "staffiq:documents:create";
    private static final String PERM_UPDATE = "staffiq:documents:update";
    private static final String PERM_DELETE = "staffiq:documents:delete";

    private final DocumentRepository repository;
    private final HrmPlatformAuthorizationService platformAuthorization;


    @Override
    public List<Document> findAll(CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        if (context.getProfileId() != null) {
            return repository.findByProfileId(context.getProfileId().toString());
        }
        if (context.getOrgId() != null) {
            return repository.findByOrgId(context.getOrgId().toString());
        }
        return repository.findAll();
    }

    @Override
    public Document findById(String id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        Document row = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));
        HrmProfileContextSupport.assertReadable(row, context);
        return row;
    }

    @Override
    @Transactional
    public Document create(Document body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_CREATE);
        if (body.getId() == null || body.getId().isBlank()) {
            body.setId(UUID.randomUUID().toString());
        }
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public Document update(Document body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_UPDATE);
        Document existing = repository
                .findById(body.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public void deleteById(String id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_DELETE);
        Document existing = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        repository.deleteById(id);
    }
}
