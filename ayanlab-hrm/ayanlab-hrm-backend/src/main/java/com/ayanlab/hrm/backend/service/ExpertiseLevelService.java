package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.ExpertiseLevel;
import com.ayanlab.core.data.CoreContext;
import java.util.List;

public interface ExpertiseLevelService {
    List<ExpertiseLevel> findAll(CoreContext context);
    ExpertiseLevel findById(String id, CoreContext context);
    ExpertiseLevel create(ExpertiseLevel body, CoreContext context);
    ExpertiseLevel update(ExpertiseLevel body, CoreContext context);
    void deleteById(String id, CoreContext context);
}
