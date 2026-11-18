package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.tasks.Task;
import com.ayanlab.pms.backend.repository.TaskRepository;
import com.ayanlab.pms.backend.service.ITaskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TaskService extends AbstractIpmsOrgService<Task, TaskRepository> implements ITaskService {

    public TaskService(TaskRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "Task";
    }
}
