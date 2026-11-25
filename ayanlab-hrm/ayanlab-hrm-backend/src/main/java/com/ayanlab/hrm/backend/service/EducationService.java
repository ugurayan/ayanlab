package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.Education;
import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

public interface EducationService {
    List<Education> findAll(CoreContext context);
    Education findById(UUID id, CoreContext context);
    Education create(Education body, CoreContext context);
    Education update(Education body, CoreContext context);
    void deleteById(UUID id, CoreContext context);
}
