package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.WorkExperience;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface WorkExperienceRepository extends JpaRepository<WorkExperience, UUID> {
    List<WorkExperience> findByProfileId(UUID profileId);
    List<WorkExperience> findByOrgId(UUID orgId);
    List<WorkExperience> findByWorkerId(UUID workerId);
}
