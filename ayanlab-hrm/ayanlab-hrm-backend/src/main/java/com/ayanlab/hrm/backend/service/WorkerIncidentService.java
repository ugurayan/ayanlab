package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.WorkerIncident;
import com.ayanlab.core.data.CoreContext;
import java.util.List;

public interface WorkerIncidentService {
    List<WorkerIncident> findAll(CoreContext context);
    WorkerIncident findById(String id, CoreContext context);
    WorkerIncident create(WorkerIncident body, CoreContext context);
    WorkerIncident update(WorkerIncident body, CoreContext context);
    void deleteById(String id, CoreContext context);
}
