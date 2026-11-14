package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.leasing.Tenant;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TenantRepository extends IpmsOrgRepository<Tenant, UUID> {

    List<Tenant> findByOrganizationIdAndPropertyIdAndIsActiveTrueOrderByUpdatedAtDesc(
            UUID organizationId, UUID propertyId);

    List<Tenant> findByOrganizationIdAndUnitIdAndIsActiveTrueOrderByUpdatedAtDesc(
            UUID organizationId, UUID unitId);

    List<Tenant> findByOrganizationIdAndLeaseIdAndIsActiveTrueOrderByUpdatedAtDesc(
            UUID organizationId, UUID leaseId);
}
