package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.Worker;
import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

public interface WorkerService {
    List<Worker> findAll(CoreContext context);
    Worker findById(UUID id, CoreContext context);
    Worker create(Worker body, CoreContext context);
    Worker update(Worker body, CoreContext context);
    void deleteById(UUID id, CoreContext context);
}
