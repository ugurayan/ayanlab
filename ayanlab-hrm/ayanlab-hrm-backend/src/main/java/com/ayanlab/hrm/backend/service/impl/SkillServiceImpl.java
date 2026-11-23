package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.hrm.backend.entity.Skill;
import com.ayanlab.hrm.backend.repository.SkillRepository;
import com.ayanlab.hrm.backend.service.SkillService;
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
public class SkillServiceImpl implements SkillService {

    private static final String PERM_READ = "staffiq:skills:read";
    private static final String PERM_CREATE = "staffiq:skills:create";
    private static final String PERM_UPDATE = "staffiq:skills:update";
    private static final String PERM_DELETE = "staffiq:skills:delete";

    private final SkillRepository repository;
    private final HrmPlatformAuthorizationService platformAuthorization;


    @Override
    public List<Skill> findAll(CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        return repository.findAll();
    }

    @Override
    public Skill findById(String id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        Skill row = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Skill not found"));
        HrmProfileContextSupport.assertReadable(row, context);
        return row;
    }

    @Override
    @Transactional
    public Skill create(Skill body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_CREATE);
        if (body.getId() == null || body.getId().isBlank()) {
            body.setId(UUID.randomUUID().toString());
        }
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public Skill update(Skill body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_UPDATE);
        Skill existing = repository
                .findById(body.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Skill not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public void deleteById(String id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_DELETE);
        Skill existing = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Skill not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        repository.deleteById(id);
    }
}
