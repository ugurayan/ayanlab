package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.ExpertiseLevel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpertiseLevelRepository extends JpaRepository<ExpertiseLevel, String> {
    List<ExpertiseLevel> findByProfileId(String profileId);
    List<ExpertiseLevel> findByOrgId(String orgId);
    List<ExpertiseLevel> findByWorkerId(String workerId);
}
