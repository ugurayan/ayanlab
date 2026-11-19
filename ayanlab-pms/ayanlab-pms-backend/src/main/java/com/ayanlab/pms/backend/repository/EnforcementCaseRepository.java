package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.tr.EnforcementCase;
import org.springframework.stereotype.Repository;

@Repository
public interface EnforcementCaseRepository extends IpmsOrgRepository<EnforcementCase, java.util.UUID> {
}
