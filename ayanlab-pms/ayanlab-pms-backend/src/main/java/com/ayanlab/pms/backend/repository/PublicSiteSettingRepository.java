package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.settings.PublicSiteSetting;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicSiteSettingRepository extends IpmsOrgRepository<PublicSiteSetting, java.util.UUID> {
}
