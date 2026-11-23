package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.JobRole;
import com.ayanlab.core.data.CoreContext;
import java.util.List;

public interface JobRoleService {
    List<JobRole> findAll(CoreContext context);
    JobRole findById(String id, CoreContext context);
    JobRole create(JobRole body, CoreContext context);
    JobRole update(JobRole body, CoreContext context);
    void deleteById(String id, CoreContext context);
}
