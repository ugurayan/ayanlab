package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.accounting.ChartOfAccount;
import org.springframework.stereotype.Repository;

@Repository
public interface ChartOfAccountRepository extends IpmsOrgRepository<ChartOfAccount, java.util.UUID> {
}
