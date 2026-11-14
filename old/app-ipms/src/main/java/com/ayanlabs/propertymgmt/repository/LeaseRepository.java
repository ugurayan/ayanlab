package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.leasing.Lease;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaseRepository extends IpmsOrgRepository<Lease, java.util.UUID> {
    java.util.List<Lease> findByOrganizationIdAndPropertyIdAndIsActiveTrueOrderByUpdatedAtDesc(
            java.util.UUID organizationId, java.util.UUID propertyId);

    java.util.List<Lease> findByOrganizationIdAndUnitIdAndIsActiveTrueOrderByUpdatedAtDesc(
            java.util.UUID organizationId, java.util.UUID unitId);

    java.util.List<Lease> findByOrganizationIdAndTenantProfileIdAndIsActiveTrueOrderByUpdatedAtDesc(
            java.util.UUID organizationId, java.util.UUID tenantProfileId);
}
