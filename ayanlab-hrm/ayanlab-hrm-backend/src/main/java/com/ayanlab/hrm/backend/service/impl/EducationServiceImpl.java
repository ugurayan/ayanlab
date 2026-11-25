package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.hrm.backend.entity.Education;
import com.ayanlab.hrm.backend.repository.EducationRepository;
import com.ayanlab.hrm.backend.service.EducationService;
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
public class EducationServiceImpl implements EducationService {

    private static final String PERM_READ = "staffiq:education:read";
    private static final String PERM_CREATE = "staffiq:education:create";
    private static final String PERM_UPDATE = "staffiq:education:update";
    private static final String PERM_DELETE = "staffiq:education:delete";

    private final EducationRepository repository;
    private final HrmPlatformAuthorizationService platformAuthorization;


    @Override
    public List<Education> findAll(CoreContext context) {
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
    public Education findById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        Education row = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Education not found"));
        HrmProfileContextSupport.assertReadable(row, context);
        return row;
    }

    @Override
    @Transactional
    public Education create(Education body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_CREATE);
        if (body.getId() == null) {
            body.setId(UUID.randomUUID());
        }
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public Education update(Education body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_UPDATE);
        Education existing = repository
                .findById(body.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Education not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_DELETE);
        Education existing = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Education not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        repository.deleteById(id);
    }
}
