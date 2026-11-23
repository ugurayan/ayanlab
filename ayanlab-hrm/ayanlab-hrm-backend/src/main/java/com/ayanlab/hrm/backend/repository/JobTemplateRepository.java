package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.JobTemplate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobTemplateRepository extends JpaRepository<JobTemplate, String> {
    List<JobTemplate> findByProfileId(String profileId);
    List<JobTemplate> findByOrgId(String orgId);
    List<JobTemplate> findByWorkerId(String workerId);
}
