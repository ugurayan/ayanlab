package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.hrm.backend.entity.DressCode;
import com.ayanlab.hrm.backend.repository.DressCodeRepository;
import com.ayanlab.hrm.backend.service.DressCodeService;
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
public class DressCodeServiceImpl implements DressCodeService {

    private static final String PERM_READ = "staffiq:dress_codes:read";
    private static final String PERM_CREATE = "staffiq:dress_codes:create";
    private static final String PERM_UPDATE = "staffiq:dress_codes:update";
    private static final String PERM_DELETE = "staffiq:dress_codes:delete";

    private final DressCodeRepository repository;
    private final HrmPlatformAuthorizationService platformAuthorization;


    @Override
    public List<DressCode> findAll(CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        return repository.findAll();
    }

    @Override
    public DressCode findById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        DressCode row = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DressCode not found"));
        HrmProfileContextSupport.assertReadable(row, context);
        return row;
    }

    @Override
    @Transactional
    public DressCode create(DressCode body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_CREATE);
        if (body.getId() == null) {
            body.setId(UUID.randomUUID());
        }
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public DressCode update(DressCode body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_UPDATE);
        DressCode existing = repository
                .findById(body.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DressCode not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_DELETE);
        DressCode existing = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DressCode not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        repository.deleteById(id);
    }
}
