package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.settings.ResidentCenterContact;
import org.springframework.stereotype.Repository;

@Repository
public interface ResidentCenterContactRepository extends IpmsOrgRepository<ResidentCenterContact, java.util.UUID> {
}
