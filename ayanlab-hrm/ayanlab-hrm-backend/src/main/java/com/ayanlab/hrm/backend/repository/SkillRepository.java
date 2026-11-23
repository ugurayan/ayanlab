package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.Skill;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillRepository extends JpaRepository<Skill, String> {
    List<Skill> findByProfileId(String profileId);
    List<Skill> findByOrgId(String orgId);
    List<Skill> findByWorkerId(String workerId);
}
