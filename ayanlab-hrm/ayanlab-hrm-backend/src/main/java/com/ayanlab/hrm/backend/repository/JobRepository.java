package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.Job;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, String> {
    List<Job> findByProfileId(String profileId);
    List<Job> findByOrgId(String orgId);
    List<Job> findByWorkerId(String workerId);
}
