package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.settings.OrgSetting;
import org.springframework.stereotype.Repository;

@Repository
public interface OrgSettingRepository extends IpmsOrgRepository<OrgSetting, java.util.UUID> {
}
