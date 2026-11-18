package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.associations.Violation;
import org.springframework.stereotype.Repository;

@Repository
public interface ViolationRepository extends IpmsOrgRepository<Violation, java.util.UUID> {
    java.util.List<Violation> findByOrganizationIdAndAssociationIdAndIsActiveTrueOrderByUpdatedAtDesc(
            java.util.UUID organizationId, java.util.UUID associationId);
}
