package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.hrm.backend.entity.ExpertiseLevel;
import com.ayanlab.hrm.backend.repository.ExpertiseLevelRepository;
import com.ayanlab.hrm.backend.service.ExpertiseLevelService;
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
public class ExpertiseLevelServiceImpl implements ExpertiseLevelService {

    private static final String PERM_READ = "staffiq:expertise_levels:read";
    private static final String PERM_CREATE = "staffiq:expertise_levels:create";
    private static final String PERM_UPDATE = "staffiq:expertise_levels:update";
    private static final String PERM_DELETE = "staffiq:expertise_levels:delete";

    private final ExpertiseLevelRepository repository;
    private final HrmPlatformAuthorizationService platformAuthorization;


    @Override
    public List<ExpertiseLevel> findAll(CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        return repository.findAll();
    }

    @Override
    public ExpertiseLevel findById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        ExpertiseLevel row = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ExpertiseLevel not found"));
        HrmProfileContextSupport.assertReadable(row, context);
        return row;
    }

    @Override
    @Transactional
    public ExpertiseLevel create(ExpertiseLevel body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_CREATE);
        if (body.getId() == null) {
            body.setId(UUID.randomUUID());
        }
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public ExpertiseLevel update(ExpertiseLevel body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_UPDATE);
        ExpertiseLevel existing = repository
                .findById(body.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ExpertiseLevel not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_DELETE);
        ExpertiseLevel existing = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ExpertiseLevel not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        repository.deleteById(id);
    }
}
