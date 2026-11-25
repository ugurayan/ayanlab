package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.JobRole;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface JobRoleRepository extends JpaRepository<JobRole, UUID> {
    List<JobRole> findByProfileId(UUID profileId);
    List<JobRole> findByOrgId(UUID orgId);
    List<JobRole> findByWorkerId(UUID workerId);
}
