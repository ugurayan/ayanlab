package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.Skill;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SkillRepository extends JpaRepository<Skill, UUID> {
    List<Skill> findByProfileId(UUID profileId);
    List<Skill> findByOrgId(UUID orgId);
    List<Skill> findByWorkerId(UUID workerId);
}
