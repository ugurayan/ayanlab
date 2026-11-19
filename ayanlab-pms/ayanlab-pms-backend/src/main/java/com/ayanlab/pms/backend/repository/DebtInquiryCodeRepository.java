package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.tr.DebtInquiryCode;
import org.springframework.stereotype.Repository;

@Repository
public interface DebtInquiryCodeRepository extends IpmsOrgRepository<DebtInquiryCode, java.util.UUID> {
}
