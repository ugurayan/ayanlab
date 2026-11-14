package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.settings.ResidentCenterUser;
import org.springframework.stereotype.Repository;

@Repository
public interface ResidentCenterUserRepository extends IpmsOrgRepository<ResidentCenterUser, java.util.UUID> {
}
