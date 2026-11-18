package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.leasing.DraftLease;
import org.springframework.stereotype.Repository;

@Repository
public interface DraftLeaseRepository extends IpmsOrgRepository<DraftLease, java.util.UUID> {
    java.util.List<DraftLease> findByOrganizationIdAndPropertyIdAndIsActiveTrueOrderByUpdatedAtDesc(
            java.util.UUID organizationId, java.util.UUID propertyId);

    java.util.List<DraftLease> findByOrganizationIdAndUnitIdAndIsActiveTrueOrderByUpdatedAtDesc(
            java.util.UUID organizationId, java.util.UUID unitId);
}
