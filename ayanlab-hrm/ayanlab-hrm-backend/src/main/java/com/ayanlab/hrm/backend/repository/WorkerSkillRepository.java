package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.WorkerSkill;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkerSkillRepository extends JpaRepository<WorkerSkill, String> {
    List<WorkerSkill> findByProfileId(String profileId);
    List<WorkerSkill> findByOrgId(String orgId);
    List<WorkerSkill> findByWorkerId(String workerId);
}
