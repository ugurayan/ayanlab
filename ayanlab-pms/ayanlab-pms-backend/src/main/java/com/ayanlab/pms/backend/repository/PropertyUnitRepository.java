package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.rentals.PropertyUnit;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyUnitRepository extends IpmsOrgRepository<PropertyUnit, java.util.UUID> {
    java.util.List<PropertyUnit> findByOrganizationIdAndPropertyIdAndIsActiveTrueOrderByUpdatedAtDesc(
            java.util.UUID organizationId, java.util.UUID propertyId);
}
