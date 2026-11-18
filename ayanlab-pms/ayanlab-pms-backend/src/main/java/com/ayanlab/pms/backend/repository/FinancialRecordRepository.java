package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.accounting.FinancialRecord;
import org.springframework.stereotype.Repository;

@Repository
public interface FinancialRecordRepository extends IpmsOrgRepository<FinancialRecord, java.util.UUID> {
    java.util.List<FinancialRecord> findByOrganizationIdAndPropertyIdAndIsActiveTrueOrderByUpdatedAtDesc(
            java.util.UUID organizationId, java.util.UUID propertyId);
}
