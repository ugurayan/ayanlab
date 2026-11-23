package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.Education;
import com.ayanlab.core.data.CoreContext;
import java.util.List;

public interface EducationService {
    List<Education> findAll(CoreContext context);
    Education findById(String id, CoreContext context);
    Education create(Education body, CoreContext context);
    Education update(Education body, CoreContext context);
    void deleteById(String id, CoreContext context);
}
