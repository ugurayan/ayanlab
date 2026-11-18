package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.leasing.Applicant;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicantRepository extends IpmsOrgRepository<Applicant, java.util.UUID> {
    java.util.List<Applicant> findByOrganizationIdAndListingIdAndIsActiveTrueOrderByUpdatedAtDesc(
            java.util.UUID organizationId, java.util.UUID listingId);
}
