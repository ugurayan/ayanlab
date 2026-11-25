package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.ExpertiseLevel;
import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

public interface ExpertiseLevelService {
    List<ExpertiseLevel> findAll(CoreContext context);
    ExpertiseLevel findById(UUID id, CoreContext context);
    ExpertiseLevel create(ExpertiseLevel body, CoreContext context);
    ExpertiseLevel update(ExpertiseLevel body, CoreContext context);
    void deleteById(UUID id, CoreContext context);
}
