package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.FreeTimeWindow;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface FreeTimeWindowRepository extends JpaRepository<FreeTimeWindow, UUID> {
    List<FreeTimeWindow> findByProfileId(UUID profileId);
    List<FreeTimeWindow> findByOrgId(UUID orgId);
    List<FreeTimeWindow> findByWorkerId(UUID workerId);
}
