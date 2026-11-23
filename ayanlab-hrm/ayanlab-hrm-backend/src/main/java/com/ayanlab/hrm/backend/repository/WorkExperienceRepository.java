package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.WorkExperience;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkExperienceRepository extends JpaRepository<WorkExperience, String> {
    List<WorkExperience> findByProfileId(String profileId);
    List<WorkExperience> findByOrgId(String orgId);
    List<WorkExperience> findByWorkerId(String workerId);
}
