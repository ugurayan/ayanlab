package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.JobRoleCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface JobRoleCategoryRepository extends JpaRepository<JobRoleCategory, UUID> {
    List<JobRoleCategory> findByProfileId(UUID profileId);
    List<JobRoleCategory> findByOrgId(UUID orgId);
    List<JobRoleCategory> findByWorkerId(UUID workerId);
}
