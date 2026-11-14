package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.settings.ResidentCenterContact;
import org.springframework.stereotype.Repository;

@Repository
public interface ResidentCenterContactRepository extends IpmsOrgRepository<ResidentCenterContact, java.util.UUID> {
}
