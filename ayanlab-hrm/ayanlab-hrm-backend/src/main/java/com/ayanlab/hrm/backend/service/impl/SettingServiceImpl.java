package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.hrm.backend.entity.Setting;
import com.ayanlab.hrm.backend.repository.SettingRepository;
import com.ayanlab.hrm.backend.service.SettingService;
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
public class SettingServiceImpl implements SettingService {

    private static final String PERM_READ = "staffiq:settings:read";
    private static final String PERM_CREATE = "staffiq:settings:create";
    private static final String PERM_UPDATE = "staffiq:settings:update";
    private static final String PERM_DELETE = "staffiq:settings:delete";

    private final SettingRepository repository;
    private final HrmPlatformAuthorizationService platformAuthorization;


    @Override
    public List<Setting> findAll(CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        return repository.findAll();
    }

    @Override
    public Setting findById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        Setting row = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Setting not found"));
        HrmProfileContextSupport.assertReadable(row, context);
        return row;
    }

    @Override
    @Transactional
    public Setting create(Setting body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_CREATE);
        if (body.getId() == null) {
            body.setId(UUID.randomUUID());
        }
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public Setting update(Setting body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_UPDATE);
        Setting existing = repository
                .findById(body.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Setting not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_DELETE);
        Setting existing = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Setting not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        repository.deleteById(id);
    }
}
