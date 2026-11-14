package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.propertymgmt.entity.tasks.RecurringTask;
import com.ayanlabs.propertymgmt.repository.RecurringTaskRepository;
import com.ayanlabs.propertymgmt.service.IRecurringTaskService;
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
