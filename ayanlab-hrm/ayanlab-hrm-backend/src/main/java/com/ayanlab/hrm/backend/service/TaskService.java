package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.Task;
import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

public interface TaskService {
    List<Task> findAll(CoreContext context);
    Task findById(UUID id, CoreContext context);
    Task create(Task body, CoreContext context);
    Task update(Task body, CoreContext context);
    void deleteById(UUID id, CoreContext context);
}
