package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.UserAudit;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAuditRepository extends JpaRepository<UserAudit, String> {
    List<UserAudit> findByProfileId(String profileId);
    List<UserAudit> findByOrgId(String orgId);
    List<UserAudit> findByWorkerId(String workerId);
}
