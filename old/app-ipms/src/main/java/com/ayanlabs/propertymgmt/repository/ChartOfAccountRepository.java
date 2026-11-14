package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.accounting.ChartOfAccount;
import org.springframework.stereotype.Repository;

@Repository
public interface ChartOfAccountRepository extends IpmsOrgRepository<ChartOfAccount, java.util.UUID> {
}
