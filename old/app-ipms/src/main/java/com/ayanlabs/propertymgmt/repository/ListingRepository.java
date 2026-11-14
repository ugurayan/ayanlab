package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.leasing.Listing;
import org.springframework.stereotype.Repository;

@Repository
public interface ListingRepository extends IpmsOrgRepository<Listing, java.util.UUID> {
    java.util.List<Listing> findByOrganizationIdAndPropertyIdAndIsActiveTrueOrderByUpdatedAtDesc(
            java.util.UUID organizationId, java.util.UUID propertyId);
}
