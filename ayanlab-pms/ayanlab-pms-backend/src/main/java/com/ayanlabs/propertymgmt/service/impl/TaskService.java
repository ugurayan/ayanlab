package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.propertymgmt.entity.tasks.Task;
import com.ayanlabs.propertymgmt.repository.TaskRepository;
import com.ayanlabs.propertymgmt.service.ITaskService;
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
