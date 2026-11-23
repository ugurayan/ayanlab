package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.hrm.backend.entity.ContactType;
import com.ayanlab.hrm.backend.repository.ContactTypeRepository;
import com.ayanlab.hrm.backend.service.ContactTypeService;
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
public class ContactTypeServiceImpl implements ContactTypeService {

    private static final String PERM_READ = "staffiq:contact_types:read";
    private static final String PERM_CREATE = "staffiq:contact_types:create";
    private static final String PERM_UPDATE = "staffiq:contact_types:update";
    private static final String PERM_DELETE = "staffiq:contact_types:delete";

    private final ContactTypeRepository repository;
    private final HrmPlatformAuthorizationService platformAuthorization;


    @Override
    public List<ContactType> findAll(CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        return repository.findAll();
    }

    @Override
    public ContactType findById(String id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        ContactType row = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ContactType not found"));
        HrmProfileContextSupport.assertReadable(row, context);
        return row;
    }

    @Override
    @Transactional
    public ContactType create(ContactType body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_CREATE);
        if (body.getId() == null || body.getId().isBlank()) {
            body.setId(UUID.randomUUID().toString());
        }
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public ContactType update(ContactType body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_UPDATE);
        ContactType existing = repository
                .findById(body.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ContactType not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public void deleteById(String id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_DELETE);
        ContactType existing = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ContactType not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        repository.deleteById(id);
    }
}
