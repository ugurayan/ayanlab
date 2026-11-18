package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.maintenance.Vendor;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorRepository extends IpmsOrgRepository<Vendor, java.util.UUID> {
    java.util.List<Vendor> findByOrganizationIdAndProfileIdAndIsActiveTrue(
            java.util.UUID organizationId, java.util.UUID profileId);
}
