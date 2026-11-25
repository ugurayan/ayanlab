package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.WorkerSkill;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface WorkerSkillRepository extends JpaRepository<WorkerSkill, UUID> {
    List<WorkerSkill> findByProfileId(UUID profileId);
    List<WorkerSkill> findByOrgId(UUID orgId);
    List<WorkerSkill> findByWorkerId(UUID workerId);
}
