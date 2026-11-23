package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.Skill;
import com.ayanlab.core.data.CoreContext;
import java.util.List;

public interface SkillService {
    List<Skill> findAll(CoreContext context);
    Skill findById(String id, CoreContext context);
    Skill create(Skill body, CoreContext context);
    Skill update(Skill body, CoreContext context);
    void deleteById(String id, CoreContext context);
}
