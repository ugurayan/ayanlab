package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.JobTemplate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface JobTemplateRepository extends JpaRepository<JobTemplate, UUID> {
    List<JobTemplate> findByProfileId(UUID profileId);
    List<JobTemplate> findByOrgId(UUID orgId);
    List<JobTemplate> findByWorkerId(UUID workerId);
}
