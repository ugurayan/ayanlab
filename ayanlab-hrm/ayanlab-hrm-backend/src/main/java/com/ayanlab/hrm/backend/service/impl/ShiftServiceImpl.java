package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.hrm.backend.entity.Shift;
import com.ayanlab.hrm.backend.repository.ShiftRepository;
import com.ayanlab.hrm.backend.service.ShiftService;
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
public class ShiftServiceImpl implements ShiftService {

    private static final String PERM_READ = "staffiq:shifts:read";
    private static final String PERM_CREATE = "staffiq:shifts:create";
    private static final String PERM_UPDATE = "staffiq:shifts:update";
    private static final String PERM_DELETE = "staffiq:shifts:delete";

    private final ShiftRepository repository;
    private final HrmPlatformAuthorizationService platformAuthorization;


    @Override
    public List<Shift> findAll(CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        return repository.findAll();
    }

    @Override
    public Shift findById(String id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        return repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shift not found"));
    }

    @Override
    @Transactional
    public Shift create(Shift body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_CREATE);
        if (body.getId() == null || body.getId().isBlank()) {
            body.setId(UUID.randomUUID().toString());
        }
        return repository.save(body);
    }

    @Override
    @Transactional
    public Shift update(Shift body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_UPDATE);
        if (!repository.existsById(body.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Shift not found");
        }
        return repository.save(body);
    }

    @Override
    @Transactional
    public void deleteById(String id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_DELETE);
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Shift not found");
        }
        repository.deleteById(id);
    }
}
