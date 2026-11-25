package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.WorkerSkill;
import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

public interface WorkerSkillService {
    List<WorkerSkill> findAll(CoreContext context);
    WorkerSkill findById(UUID id, CoreContext context);
    WorkerSkill create(WorkerSkill body, CoreContext context);
    WorkerSkill update(WorkerSkill body, CoreContext context);
    void deleteById(UUID id, CoreContext context);
}
