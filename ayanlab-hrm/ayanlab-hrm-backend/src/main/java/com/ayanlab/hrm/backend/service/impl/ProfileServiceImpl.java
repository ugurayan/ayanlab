package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.hrm.backend.entity.Profile;
import com.ayanlab.hrm.backend.repository.ProfileRepository;
import com.ayanlab.hrm.backend.service.ProfileService;
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
public class ProfileServiceImpl implements ProfileService {

    private static final String PERM_READ = "staffiq:profile:read";
    private static final String PERM_CREATE = "staffiq:profile:create";
    private static final String PERM_UPDATE = "staffiq:profile:update";
    private static final String PERM_DELETE = "staffiq:profile:delete";

    private final ProfileRepository repository;
    private final HrmPlatformAuthorizationService platformAuthorization;


    @Override
    public List<Profile> findAll(CoreContext context) {
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
    public Profile findById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        Profile row = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));
        HrmProfileContextSupport.assertReadable(row, context);
        return row;
    }

    @Override
    @Transactional
    public Profile create(Profile body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_CREATE);
        if (body.getId() == null) {
            body.setId(UUID.randomUUID());
        }
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public Profile update(Profile body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_UPDATE);
        Profile existing = repository
                .findById(body.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_DELETE);
        Profile existing = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        repository.deleteById(id);
    }
}
