package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.Setting;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SettingRepository extends JpaRepository<Setting, UUID> {
    List<Setting> findByProfileId(UUID profileId);
    List<Setting> findByOrgId(UUID orgId);
    List<Setting> findByWorkerId(UUID workerId);
}
