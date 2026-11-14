package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.communication.AutomatedEmailSetting;
import org.springframework.stereotype.Repository;

@Repository
public interface AutomatedEmailSettingRepository extends IpmsOrgRepository<AutomatedEmailSetting, java.util.UUID> {
}
