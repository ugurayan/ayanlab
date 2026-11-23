package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.Setting;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingRepository extends JpaRepository<Setting, String> {
    List<Setting> findByProfileId(String profileId);
    List<Setting> findByOrgId(String orgId);
    List<Setting> findByWorkerId(String workerId);
}
