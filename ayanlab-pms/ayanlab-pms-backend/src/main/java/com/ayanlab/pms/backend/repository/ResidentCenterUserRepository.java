package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.settings.ResidentCenterUser;
import org.springframework.stereotype.Repository;

@Repository
public interface ResidentCenterUserRepository extends IpmsOrgRepository<ResidentCenterUser, java.util.UUID> {
}
