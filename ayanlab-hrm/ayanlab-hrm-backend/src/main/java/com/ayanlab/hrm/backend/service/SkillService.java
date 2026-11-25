package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.Skill;
import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

public interface SkillService {
    List<Skill> findAll(CoreContext context);
    Skill findById(UUID id, CoreContext context);
    Skill create(Skill body, CoreContext context);
    Skill update(Skill body, CoreContext context);
    void deleteById(UUID id, CoreContext context);
}
