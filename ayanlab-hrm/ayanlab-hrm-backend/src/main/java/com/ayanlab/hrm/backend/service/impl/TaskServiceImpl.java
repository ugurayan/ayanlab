package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.hrm.backend.entity.Task;
import com.ayanlab.hrm.backend.repository.TaskRepository;
import com.ayanlab.hrm.backend.service.TaskService;
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
public class TaskServiceImpl implements TaskService {

    private static final String PERM_READ = "staffiq:tasks:read";
    private static final String PERM_CREATE = "staffiq:tasks:create";
    private static final String PERM_UPDATE = "staffiq:tasks:update";
    private static final String PERM_DELETE = "staffiq:tasks:delete";

    private final TaskRepository repository;
    private final HrmPlatformAuthorizationService platformAuthorization;


    @Override
    public List<Task> findAll(CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        return repository.findAll();
    }

    @Override
    public Task findById(String id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        Task row = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        HrmProfileContextSupport.assertReadable(row, context);
        return row;
    }

    @Override
    @Transactional
    public Task create(Task body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_CREATE);
        if (body.getId() == null || body.getId().isBlank()) {
            body.setId(UUID.randomUUID().toString());
        }
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public Task update(Task body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_UPDATE);
        Task existing = repository
                .findById(body.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public void deleteById(String id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_DELETE);
        Task existing = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        repository.deleteById(id);
    }
}
