package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.Worker;
import com.ayanlab.core.data.CoreContext;
import java.util.List;

public interface WorkerService {
    List<Worker> findAll(CoreContext context);
    Worker findById(String id, CoreContext context);
    Worker create(Worker body, CoreContext context);
    Worker update(Worker body, CoreContext context);
    void deleteById(String id, CoreContext context);
}
