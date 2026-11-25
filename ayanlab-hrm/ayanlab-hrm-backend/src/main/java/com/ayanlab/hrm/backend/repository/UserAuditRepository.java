package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.UserAudit;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface UserAuditRepository extends JpaRepository<UserAudit, UUID> {
    List<UserAudit> findByProfileId(UUID profileId);
    List<UserAudit> findByOrgId(UUID orgId);
    List<UserAudit> findByWorkerId(UUID workerId);
}
