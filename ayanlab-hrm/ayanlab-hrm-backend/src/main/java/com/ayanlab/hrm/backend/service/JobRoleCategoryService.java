package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.JobRoleCategory;
import com.ayanlab.core.data.CoreContext;
import java.util.List;

public interface JobRoleCategoryService {
    List<JobRoleCategory> findAll(CoreContext context);
    JobRoleCategory findById(String id, CoreContext context);
    JobRoleCategory create(JobRoleCategory body, CoreContext context);
    JobRoleCategory update(JobRoleCategory body, CoreContext context);
    void deleteById(String id, CoreContext context);
}
