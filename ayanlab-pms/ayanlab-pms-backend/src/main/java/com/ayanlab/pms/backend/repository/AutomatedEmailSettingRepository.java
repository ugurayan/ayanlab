package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.communication.AutomatedEmailSetting;
import org.springframework.stereotype.Repository;

@Repository
public interface AutomatedEmailSettingRepository extends IpmsOrgRepository<AutomatedEmailSetting, java.util.UUID> {
}
