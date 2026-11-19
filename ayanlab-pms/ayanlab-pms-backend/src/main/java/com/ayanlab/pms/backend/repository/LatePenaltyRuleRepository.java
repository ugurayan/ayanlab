package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.tr.LatePenaltyRule;
import org.springframework.stereotype.Repository;

@Repository
public interface LatePenaltyRuleRepository extends IpmsOrgRepository<LatePenaltyRule, java.util.UUID> {
}
