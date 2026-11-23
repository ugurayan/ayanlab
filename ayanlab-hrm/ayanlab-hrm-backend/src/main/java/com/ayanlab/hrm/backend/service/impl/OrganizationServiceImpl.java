package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.hrm.backend.entity.Organization;
import com.ayanlab.hrm.backend.repository.OrganizationRepository;
import com.ayanlab.hrm.backend.service.OrganizationService;
import com.ayanlab.hrm.backend.service.platform.HrmPlatformAuthorizationService;
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
public class OrganizationServiceImpl implements OrganizationService {

    private static final String PERM_READ = "staffiq:organizations:read";
    private static final String PERM_CREATE = "staffiq:organizations:create";
    private static final String PERM_UPDATE = "staffiq:organizations:update";
    private static final String PERM_DELETE = "staffiq:organizations:delete";

    private final OrganizationRepository repository;
    private final HrmPlatformAuthorizationService platformAuthorization;


    @Override
    public List<Organization> findAll(CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        return repository.findAll();
    }

    @Override
    public Organization findById(String id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        return repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organization not found"));
    }

    @Override
    @Transactional
    public Organization create(Organization body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_CREATE);
        if (body.getId() == null || body.getId().isBlank()) {
            body.setId(UUID.randomUUID().toString());
        }
        return repository.save(body);
    }

    @Override
    @Transactional
    public Organization update(Organization body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_UPDATE);
        if (!repository.existsById(body.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Organization not found");
        }
        return repository.save(body);
    }

    @Override
    @Transactional
    public void deleteById(String id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_DELETE);
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Organization not found");
        }
        repository.deleteById(id);
    }
}
