package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.JobRoleCategory;
import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

public interface JobRoleCategoryService {
    List<JobRoleCategory> findAll(CoreContext context);
    JobRoleCategory findById(UUID id, CoreContext context);
    JobRoleCategory create(JobRoleCategory body, CoreContext context);
    JobRoleCategory update(JobRoleCategory body, CoreContext context);
    void deleteById(UUID id, CoreContext context);
}
