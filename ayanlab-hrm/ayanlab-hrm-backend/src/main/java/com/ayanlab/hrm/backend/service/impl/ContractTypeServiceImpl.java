package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.hrm.backend.entity.ContractType;
import com.ayanlab.hrm.backend.repository.ContractTypeRepository;
import com.ayanlab.hrm.backend.service.ContractTypeService;
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
public class ContractTypeServiceImpl implements ContractTypeService {

    private static final String PERM_READ = "staffiq:contract_types:read";
    private static final String PERM_CREATE = "staffiq:contract_types:create";
    private static final String PERM_UPDATE = "staffiq:contract_types:update";
    private static final String PERM_DELETE = "staffiq:contract_types:delete";

    private final ContractTypeRepository repository;
    private final HrmPlatformAuthorizationService platformAuthorization;


    @Override
    public List<ContractType> findAll(CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        return repository.findAll();
    }

    @Override
    public ContractType findById(String id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        ContractType row = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ContractType not found"));
        HrmProfileContextSupport.assertReadable(row, context);
        return row;
    }

    @Override
    @Transactional
    public ContractType create(ContractType body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_CREATE);
        if (body.getId() == null || body.getId().isBlank()) {
            body.setId(UUID.randomUUID().toString());
        }
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public ContractType update(ContractType body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_UPDATE);
        ContractType existing = repository
                .findById(body.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ContractType not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public void deleteById(String id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_DELETE);
        ContractType existing = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ContractType not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        repository.deleteById(id);
    }
}
