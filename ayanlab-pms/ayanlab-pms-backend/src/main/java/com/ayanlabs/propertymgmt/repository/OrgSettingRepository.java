package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.settings.OrgSetting;
import org.springframework.stereotype.Repository;

@Repository
public interface OrgSettingRepository extends IpmsOrgRepository<OrgSetting, java.util.UUID> {
}
