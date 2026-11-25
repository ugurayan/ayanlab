package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.HrmUser;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface HrmUserRepository extends JpaRepository<HrmUser, UUID> {
    List<HrmUser> findByProfileId(UUID profileId);
    List<HrmUser> findByOrgId(UUID orgId);
    List<HrmUser> findByWorkerId(UUID workerId);
}
