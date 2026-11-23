package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.WorkExperience;
import com.ayanlab.core.data.CoreContext;
import java.util.List;

public interface WorkExperienceService {
    List<WorkExperience> findAll(CoreContext context);
    WorkExperience findById(String id, CoreContext context);
    WorkExperience create(WorkExperience body, CoreContext context);
    WorkExperience update(WorkExperience body, CoreContext context);
    void deleteById(String id, CoreContext context);
}
