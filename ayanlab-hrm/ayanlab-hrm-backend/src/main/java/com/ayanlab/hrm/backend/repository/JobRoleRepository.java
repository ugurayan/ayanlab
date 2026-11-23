package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.JobRole;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRoleRepository extends JpaRepository<JobRole, String> {
    List<JobRole> findByProfileId(String profileId);
    List<JobRole> findByOrgId(String orgId);
    List<JobRole> findByWorkerId(String workerId);
}
