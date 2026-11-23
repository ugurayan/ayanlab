package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.hrm.backend.entity.Worker;
import com.ayanlab.hrm.backend.repository.WorkerRepository;
import com.ayanlab.hrm.backend.service.WorkerService;
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
public class WorkerServiceImpl implements WorkerService {

    private static final String PERM_READ = "staffiq:workers:read";
    private static final String PERM_CREATE = "staffiq:workers:create";
    private static final String PERM_UPDATE = "staffiq:workers:update";
    private static final String PERM_DELETE = "staffiq:workers:delete";

    private final WorkerRepository repository;
    private final HrmPlatformAuthorizationService platformAuthorization;


    @Override
    public List<Worker> findAll(CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        return repository.findAll();
    }

    @Override
    public Worker findById(String id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        return repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Worker not found"));
    }

    @Override
    @Transactional
    public Worker create(Worker body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_CREATE);
        if (body.getId() == null || body.getId().isBlank()) {
            body.setId(UUID.randomUUID().toString());
        }
        return repository.save(body);
    }

    @Override
    @Transactional
    public Worker update(Worker body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_UPDATE);
        if (!repository.existsById(body.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Worker not found");
        }
        return repository.save(body);
    }

    @Override
    @Transactional
    public void deleteById(String id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_DELETE);
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Worker not found");
        }
        repository.deleteById(id);
    }
}
