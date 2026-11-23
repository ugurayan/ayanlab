package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.Education;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EducationRepository extends JpaRepository<Education, String> {
    List<Education> findByProfileId(String profileId);
    List<Education> findByOrgId(String orgId);
    List<Education> findByWorkerId(String workerId);
}
