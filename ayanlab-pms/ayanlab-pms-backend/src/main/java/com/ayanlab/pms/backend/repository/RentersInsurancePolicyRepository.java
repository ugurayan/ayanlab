package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.leasing.RentersInsurancePolicy;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RentersInsurancePolicyRepository extends IpmsOrgRepository<RentersInsurancePolicy, UUID> {
    List<RentersInsurancePolicy> findByOrganizationIdAndTenantProfileIdAndIsActiveTrueOrderByUpdatedAtDesc(
            UUID organizationId, UUID tenantProfileId);

    List<RentersInsurancePolicy> findByOrganizationIdAndLeaseIdAndIsActiveTrueOrderByUpdatedAtDesc(
            UUID organizationId, UUID leaseId);
}
