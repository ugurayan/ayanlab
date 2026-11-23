package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.Task;
import com.ayanlab.core.data.CoreContext;
import java.util.List;

public interface TaskService {
    List<Task> findAll(CoreContext context);
    Task findById(String id, CoreContext context);
    Task create(Task body, CoreContext context);
    Task update(Task body, CoreContext context);
    void deleteById(String id, CoreContext context);
}
