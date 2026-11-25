package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.JobRole;
import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

public interface JobRoleService {
    List<JobRole> findAll(CoreContext context);
    JobRole findById(UUID id, CoreContext context);
    JobRole create(JobRole body, CoreContext context);
    JobRole update(JobRole body, CoreContext context);
    void deleteById(UUID id, CoreContext context);
}
