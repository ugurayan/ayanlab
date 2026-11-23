package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.RoleType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleTypeRepository extends JpaRepository<RoleType, String> {
    List<RoleType> findByProfileId(String profileId);
    List<RoleType> findByOrgId(String orgId);
    List<RoleType> findByWorkerId(String workerId);
}
