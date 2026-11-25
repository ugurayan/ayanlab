package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.Job;
import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

public interface JobService {
    List<Job> findAll(CoreContext context);
    Job findById(UUID id, CoreContext context);
    Job create(Job body, CoreContext context);
    Job update(Job body, CoreContext context);
    void deleteById(UUID id, CoreContext context);
}
