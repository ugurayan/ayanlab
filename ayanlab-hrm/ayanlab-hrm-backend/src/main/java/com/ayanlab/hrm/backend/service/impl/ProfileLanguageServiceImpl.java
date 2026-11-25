package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.hrm.backend.entity.ProfileLanguage;
import com.ayanlab.hrm.backend.repository.ProfileLanguageRepository;
import com.ayanlab.hrm.backend.service.ProfileLanguageService;
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
public class ProfileLanguageServiceImpl implements ProfileLanguageService {

    private static final String PERM_READ = "staffiq:languages:read";
    private static final String PERM_CREATE = "staffiq:languages:create";
    private static final String PERM_UPDATE = "staffiq:languages:update";
    private static final String PERM_DELETE = "staffiq:languages:delete";

    private final ProfileLanguageRepository repository;
    private final HrmPlatformAuthorizationService platformAuthorization;


    @Override
    public List<ProfileLanguage> findAll(CoreContext context) {
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
    public ProfileLanguage findById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        ProfileLanguage row = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ProfileLanguage not found"));
        HrmProfileContextSupport.assertReadable(row, context);
        return row;
    }

    @Override
    @Transactional
    public ProfileLanguage create(ProfileLanguage body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_CREATE);
        if (body.getId() == null) {
            body.setId(UUID.randomUUID());
        }
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public ProfileLanguage update(ProfileLanguage body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_UPDATE);
        ProfileLanguage existing = repository
                .findById(body.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ProfileLanguage not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_DELETE);
        ProfileLanguage existing = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ProfileLanguage not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        repository.deleteById(id);
    }
}
