package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.accounting.Bill;
import org.springframework.stereotype.Repository;

@Repository
public interface BillRepository extends IpmsOrgRepository<Bill, java.util.UUID> {
    java.util.List<Bill> findByOrganizationIdAndPropertyIdAndIsActiveTrueOrderByUpdatedAtDesc(
            java.util.UUID organizationId, java.util.UUID propertyId);

    java.util.List<Bill> findByOrganizationIdAndVendorIdAndIsActiveTrueOrderByUpdatedAtDesc(
            java.util.UUID organizationId, java.util.UUID vendorId);
}
