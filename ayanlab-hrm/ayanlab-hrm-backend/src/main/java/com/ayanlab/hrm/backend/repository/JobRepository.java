package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.Job;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID> {
    List<Job> findByProfileId(UUID profileId);
    List<Job> findByOrgId(UUID orgId);
    List<Job> findByWorkerId(UUID workerId);
}
