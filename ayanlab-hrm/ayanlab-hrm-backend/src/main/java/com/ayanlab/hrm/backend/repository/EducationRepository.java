package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.Education;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface EducationRepository extends JpaRepository<Education, UUID> {
    List<Education> findByProfileId(UUID profileId);
    List<Education> findByOrgId(UUID orgId);
    List<Education> findByWorkerId(UUID workerId);
}
