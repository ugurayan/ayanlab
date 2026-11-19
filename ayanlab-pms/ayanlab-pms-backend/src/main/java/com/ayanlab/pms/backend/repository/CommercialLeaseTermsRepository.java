package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.leasing.CommercialLeaseTerms;
import org.springframework.stereotype.Repository;

@Repository
public interface CommercialLeaseTermsRepository extends IpmsOrgRepository<CommercialLeaseTerms, java.util.UUID> {
}
