package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.SystemAudit;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SystemAuditRepository extends JpaRepository<SystemAudit, UUID> {
    List<SystemAudit> findByProfileId(UUID profileId);
    List<SystemAudit> findByOrgId(UUID orgId);
    List<SystemAudit> findByWorkerId(UUID workerId);
}
