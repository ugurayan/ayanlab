package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.RoleType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface RoleTypeRepository extends JpaRepository<RoleType, UUID> {
    List<RoleType> findByProfileId(UUID profileId);
    List<RoleType> findByOrgId(UUID orgId);
    List<RoleType> findByWorkerId(UUID workerId);
}
