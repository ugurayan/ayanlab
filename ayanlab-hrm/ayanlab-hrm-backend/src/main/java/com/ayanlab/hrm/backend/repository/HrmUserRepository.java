package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.HrmUser;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HrmUserRepository extends JpaRepository<HrmUser, String> {
    List<HrmUser> findByProfileId(String profileId);
    List<HrmUser> findByOrgId(String orgId);
    List<HrmUser> findByWorkerId(String workerId);
}
