package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.maintenance.FieldStaffPresence;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FieldStaffPresenceRepository extends IpmsOrgRepository<FieldStaffPresence, UUID> {
    Optional<FieldStaffPresence> findByOrganizationIdAndProfileIdAndIsActiveTrue(
            UUID organizationId, UUID profileId);
}
