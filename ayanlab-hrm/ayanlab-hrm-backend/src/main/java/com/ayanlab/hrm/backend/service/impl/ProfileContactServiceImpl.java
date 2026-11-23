package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.hrm.backend.entity.ProfileContact;
import com.ayanlab.hrm.backend.repository.ProfileContactRepository;
import com.ayanlab.hrm.backend.service.ProfileContactService;
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
public class ProfileContactServiceImpl implements ProfileContactService {

    private static final String PERM_READ = "staffiq:contacts:read";
    private static final String PERM_CREATE = "staffiq:contacts:create";
    private static final String PERM_UPDATE = "staffiq:contacts:update";
    private static final String PERM_DELETE = "staffiq:contacts:delete";

    private final ProfileContactRepository repository;
    private final HrmPlatformAuthorizationService platformAuthorization;


    @Override
    public List<ProfileContact> findAll(CoreContext context) {
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
    public ProfileContact findById(String id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        ProfileContact row = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ProfileContact not found"));
        HrmProfileContextSupport.assertReadable(row, context);
        return row;
    }

    @Override
    @Transactional
    public ProfileContact create(ProfileContact body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_CREATE);
        if (body.getId() == null || body.getId().isBlank()) {
            body.setId(UUID.randomUUID().toString());
        }
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public ProfileContact update(ProfileContact body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_UPDATE);
        ProfileContact existing = repository
                .findById(body.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ProfileContact not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public void deleteById(String id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_DELETE);
        ProfileContact existing = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ProfileContact not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        repository.deleteById(id);
    }
}
