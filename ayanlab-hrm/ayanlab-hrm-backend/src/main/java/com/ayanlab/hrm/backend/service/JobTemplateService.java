package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.JobTemplate;
import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

public interface JobTemplateService {
    List<JobTemplate> findAll(CoreContext context);
    JobTemplate findById(UUID id, CoreContext context);
    JobTemplate create(JobTemplate body, CoreContext context);
    JobTemplate update(JobTemplate body, CoreContext context);
    void deleteById(UUID id, CoreContext context);
}
