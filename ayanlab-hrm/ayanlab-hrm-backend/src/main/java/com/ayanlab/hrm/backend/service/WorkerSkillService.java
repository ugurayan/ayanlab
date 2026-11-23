package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.WorkerSkill;
import com.ayanlab.core.data.CoreContext;
import java.util.List;

public interface WorkerSkillService {
    List<WorkerSkill> findAll(CoreContext context);
    WorkerSkill findById(String id, CoreContext context);
    WorkerSkill create(WorkerSkill body, CoreContext context);
    WorkerSkill update(WorkerSkill body, CoreContext context);
    void deleteById(String id, CoreContext context);
}
