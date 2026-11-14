package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.leasing.LeaseRenewal;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaseRenewalRepository extends IpmsOrgRepository<LeaseRenewal, java.util.UUID> {
    java.util.List<LeaseRenewal> findByOrganizationIdAndLeaseIdAndIsActiveTrueOrderByUpdatedAtDesc(
            java.util.UUID organizationId, java.util.UUID leaseId);
}
