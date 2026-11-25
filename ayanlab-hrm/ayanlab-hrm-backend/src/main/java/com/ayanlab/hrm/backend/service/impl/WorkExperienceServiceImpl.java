package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.hrm.backend.entity.WorkExperience;
import com.ayanlab.hrm.backend.repository.WorkExperienceRepository;
import com.ayanlab.hrm.backend.service.WorkExperienceService;
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
public class WorkExperienceServiceImpl implements WorkExperienceService {

    private static final String PERM_READ = "staffiq:experiences:read";
    private static final String PERM_CREATE = "staffiq:experiences:create";
    private static final String PERM_UPDATE = "staffiq:experiences:update";
    private static final String PERM_DELETE = "staffiq:experiences:delete";

    private final WorkExperienceRepository repository;
    private final HrmPlatformAuthorizationService platformAuthorization;


    @Override
    public List<WorkExperience> findAll(CoreContext context) {
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
    public WorkExperience findById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        WorkExperience row = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "WorkExperience not found"));
        HrmProfileContextSupport.assertReadable(row, context);
        return row;
    }

    @Override
    @Transactional
    public WorkExperience create(WorkExperience body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_CREATE);
        if (body.getId() == null) {
            body.setId(UUID.randomUUID());
        }
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public WorkExperience update(WorkExperience body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_UPDATE);
        WorkExperience existing = repository
                .findById(body.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "WorkExperience not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_DELETE);
        WorkExperience existing = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "WorkExperience not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        repository.deleteById(id);
    }
}
