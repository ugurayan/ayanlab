package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.rentals.OutstandingBalance;
import org.springframework.stereotype.Repository;

@Repository
public interface OutstandingBalanceRepository extends IpmsOrgRepository<OutstandingBalance, java.util.UUID> {
    java.util.List<OutstandingBalance> findByOrganizationIdAndLeaseIdAndIsActiveTrueOrderByUpdatedAtDesc(
            java.util.UUID organizationId, java.util.UUID leaseId);

    java.util.List<OutstandingBalance> findByOrganizationIdAndTenantProfileIdAndIsActiveTrueOrderByUpdatedAtDesc(
            java.util.UUID organizationId, java.util.UUID tenantProfileId);
}
