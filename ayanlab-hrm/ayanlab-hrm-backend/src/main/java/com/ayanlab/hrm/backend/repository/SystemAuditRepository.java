package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.SystemAudit;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemAuditRepository extends JpaRepository<SystemAudit, String> {
    List<SystemAudit> findByProfileId(String profileId);
    List<SystemAudit> findByOrgId(String orgId);
    List<SystemAudit> findByWorkerId(String workerId);
}
