package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.settings.PublicSiteSetting;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicSiteSettingRepository extends IpmsOrgRepository<PublicSiteSetting, java.util.UUID> {
}
