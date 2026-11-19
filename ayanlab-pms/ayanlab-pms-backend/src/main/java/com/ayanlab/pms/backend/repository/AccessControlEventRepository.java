package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.tr.AccessControlEvent;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessControlEventRepository extends IpmsOrgRepository<AccessControlEvent, java.util.UUID> {
}
