package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.settings.OrgWhatsappChannelSetting;
import org.springframework.stereotype.Repository;

@Repository
public interface OrgWhatsappChannelSettingRepository extends IpmsOrgRepository<OrgWhatsappChannelSetting, java.util.UUID> {
}
