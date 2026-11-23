package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.JobRoleCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRoleCategoryRepository extends JpaRepository<JobRoleCategory, String> {
    List<JobRoleCategory> findByProfileId(String profileId);
    List<JobRoleCategory> findByOrgId(String orgId);
    List<JobRoleCategory> findByWorkerId(String workerId);
}
