package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.accounting.EftApproval;
import org.springframework.stereotype.Repository;

@Repository
public interface EftApprovalRepository extends IpmsOrgRepository<EftApproval, java.util.UUID> {
}
