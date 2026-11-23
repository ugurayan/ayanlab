package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.hrm.backend.entity.WorkerIncident;
import com.ayanlab.hrm.backend.repository.WorkerIncidentRepository;
import com.ayanlab.hrm.backend.service.WorkerIncidentService;
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
public class WorkerIncidentServiceImpl implements WorkerIncidentService {

    private static final String PERM_READ = "staffiq:workers:read";
    private static final String PERM_CREATE = "staffiq:workers:create";
    private static final String PERM_UPDATE = "staffiq:workers:update";
    private static final String PERM_DELETE = "staffiq:workers:delete";

    private final WorkerIncidentRepository repository;
    private final HrmPlatformAuthorizationService platformAuthorization;


    @Override
    public List<WorkerIncident> findAll(CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        return repository.findAll();
    }

    @Override
    public WorkerIncident findById(String id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        return repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "WorkerIncident not found"));
    }

    @Override
    @Transactional
    public WorkerIncident create(WorkerIncident body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_CREATE);
        if (body.getId() == null || body.getId().isBlank()) {
            body.setId(UUID.randomUUID().toString());
        }
        return repository.save(body);
    }

    @Override
    @Transactional
    public WorkerIncident update(WorkerIncident body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_UPDATE);
        if (!repository.existsById(body.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "WorkerIncident not found");
        }
        return repository.save(body);
    }

    @Override
    @Transactional
    public void deleteById(String id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_DELETE);
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "WorkerIncident not found");
        }
        repository.deleteById(id);
    }
}
