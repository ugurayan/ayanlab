package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.FreeTimeWindow;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FreeTimeWindowRepository extends JpaRepository<FreeTimeWindow, String> {
    List<FreeTimeWindow> findByProfileId(String profileId);
    List<FreeTimeWindow> findByOrgId(String orgId);
    List<FreeTimeWindow> findByWorkerId(String workerId);
}
