package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.hrm.backend.entity.ProfileLocationDetail;
import com.ayanlab.hrm.backend.repository.ProfileLocationDetailRepository;
import com.ayanlab.hrm.backend.service.ProfileLocationDetailService;
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
public class ProfileLocationDetailServiceImpl implements ProfileLocationDetailService {

    private static final String PERM_READ = "staffiq:profile_locations:read";
    private static final String PERM_CREATE = "staffiq:profile_locations:create";
    private static final String PERM_UPDATE = "staffiq:profile_locations:update";
    private static final String PERM_DELETE = "staffiq:profile_locations:delete";

    private final ProfileLocationDetailRepository repository;
    private final HrmPlatformAuthorizationService platformAuthorization;


    @Override
    public List<ProfileLocationDetail> findAll(CoreContext context) {
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
    public ProfileLocationDetail findById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        ProfileLocationDetail row = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ProfileLocationDetail not found"));
        HrmProfileContextSupport.assertReadable(row, context);
        return row;
    }

    @Override
    @Transactional
    public ProfileLocationDetail create(ProfileLocationDetail body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_CREATE);
        if (body.getId() == null) {
            body.setId(UUID.randomUUID());
        }
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public ProfileLocationDetail update(ProfileLocationDetail body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_UPDATE);
        ProfileLocationDetail existing = repository
                .findById(body.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ProfileLocationDetail not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_DELETE);
        ProfileLocationDetail existing = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ProfileLocationDetail not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        repository.deleteById(id);
    }
}
