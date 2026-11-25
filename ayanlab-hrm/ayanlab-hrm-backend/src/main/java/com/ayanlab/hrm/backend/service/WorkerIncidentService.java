package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.WorkerIncident;
import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

public interface WorkerIncidentService {
    List<WorkerIncident> findAll(CoreContext context);
    WorkerIncident findById(UUID id, CoreContext context);
    WorkerIncident create(WorkerIncident body, CoreContext context);
    WorkerIncident update(WorkerIncident body, CoreContext context);
    void deleteById(UUID id, CoreContext context);
}
