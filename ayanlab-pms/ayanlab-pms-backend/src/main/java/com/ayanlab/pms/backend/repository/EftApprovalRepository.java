package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.accounting.EftApproval;
import org.springframework.stereotype.Repository;

@Repository
public interface EftApprovalRepository extends IpmsOrgRepository<EftApproval, java.util.UUID> {
}
