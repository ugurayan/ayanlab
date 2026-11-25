package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.ExpertiseLevel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ExpertiseLevelRepository extends JpaRepository<ExpertiseLevel, UUID> {
    List<ExpertiseLevel> findByProfileId(UUID profileId);
    List<ExpertiseLevel> findByOrgId(UUID orgId);
    List<ExpertiseLevel> findByWorkerId(UUID workerId);
}
