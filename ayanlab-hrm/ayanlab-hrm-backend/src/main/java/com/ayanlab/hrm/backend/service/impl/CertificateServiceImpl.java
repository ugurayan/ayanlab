package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.hrm.backend.entity.Certificate;
import com.ayanlab.hrm.backend.repository.CertificateRepository;
import com.ayanlab.hrm.backend.service.CertificateService;
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
public class CertificateServiceImpl implements CertificateService {

    private static final String PERM_READ = "staffiq:certificates:read";
    private static final String PERM_CREATE = "staffiq:certificates:create";
    private static final String PERM_UPDATE = "staffiq:certificates:update";
    private static final String PERM_DELETE = "staffiq:certificates:delete";

    private final CertificateRepository repository;
    private final HrmPlatformAuthorizationService platformAuthorization;


    @Override
    public List<Certificate> findAll(CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        return repository.findAll();
    }

    @Override
    public Certificate findById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        Certificate row = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Certificate not found"));
        HrmProfileContextSupport.assertReadable(row, context);
        return row;
    }

    @Override
    @Transactional
    public Certificate create(Certificate body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_CREATE);
        if (body.getId() == null) {
            body.setId(UUID.randomUUID());
        }
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public Certificate update(Certificate body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_UPDATE);
        Certificate existing = repository
                .findById(body.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Certificate not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_DELETE);
        Certificate existing = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Certificate not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        repository.deleteById(id);
    }
}
