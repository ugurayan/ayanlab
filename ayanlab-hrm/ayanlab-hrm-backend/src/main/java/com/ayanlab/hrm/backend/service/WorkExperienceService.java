package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.WorkExperience;
import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

public interface WorkExperienceService {
    List<WorkExperience> findAll(CoreContext context);
    WorkExperience findById(UUID id, CoreContext context);
    WorkExperience create(WorkExperience body, CoreContext context);
    WorkExperience update(WorkExperience body, CoreContext context);
    void deleteById(UUID id, CoreContext context);
}
