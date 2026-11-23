package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.JobTemplate;
import com.ayanlab.core.data.CoreContext;
import java.util.List;

public interface JobTemplateService {
    List<JobTemplate> findAll(CoreContext context);
    JobTemplate findById(String id, CoreContext context);
    JobTemplate create(JobTemplate body, CoreContext context);
    JobTemplate update(JobTemplate body, CoreContext context);
    void deleteById(String id, CoreContext context);
}
