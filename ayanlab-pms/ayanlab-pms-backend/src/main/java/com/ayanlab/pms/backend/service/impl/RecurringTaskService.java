package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.pms.backend.entity.tasks.RecurringTask;
import com.ayanlab.pms.backend.repository.RecurringTaskRepository;
import com.ayanlab.pms.backend.service.IRecurringTaskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RecurringTaskService extends AbstractIpmsOrgService<RecurringTask, RecurringTaskRepository> implements IRecurringTaskService {

    public RecurringTaskService(RecurringTaskRepository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "RecurringTask";
    }
}
