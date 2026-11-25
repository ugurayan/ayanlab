package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.hrm.backend.entity.Application;
import com.ayanlab.hrm.backend.repository.ApplicationRepository;
import com.ayanlab.hrm.backend.service.ApplicationService;
import com.ayanlab.hrm.backend.service.platform.HrmPlatformAuthorizationService;
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
public class ApplicationServiceImpl implements ApplicationService {

    private static final String PERM_READ = "staffiq:applications:read";
    private static final String PERM_CREATE = "staffiq:applications:create";
    private static final String PERM_UPDATE = "staffiq:applications:update";
    private static final String PERM_DELETE = "staffiq:applications:delete";

    private final ApplicationRepository repository;
    private final HrmPlatformAuthorizationService platformAuthorization;


    @Override
    public List<Application> findAll(CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        return repository.findAll();
    }

    @Override
    public Application findById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        return repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));
    }

    @Override
    @Transactional
    public Application create(Application body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_CREATE);
        if (body.getId() == null) {
            body.setId(UUID.randomUUID());
        }
        return repository.save(body);
    }

    @Override
    @Transactional
    public Application update(Application body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_UPDATE);
        if (!repository.existsById(body.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found");
        }
        return repository.save(body);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_DELETE);
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found");
        }
        repository.deleteById(id);
    }
}
