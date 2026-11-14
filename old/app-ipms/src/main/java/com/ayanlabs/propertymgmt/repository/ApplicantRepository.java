package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.leasing.Applicant;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicantRepository extends IpmsOrgRepository<Applicant, java.util.UUID> {
    java.util.List<Applicant> findByOrganizationIdAndListingIdAndIsActiveTrueOrderByUpdatedAtDesc(
            java.util.UUID organizationId, java.util.UUID listingId);
}
