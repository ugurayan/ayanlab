package com.ayanlab.pms.backend.repository;

import com.ayanlab.pms.backend.entity.communication.Announcement;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnouncementRepository extends IpmsOrgRepository<Announcement, java.util.UUID> {
    java.util.List<Announcement> findByOrganizationIdAndAssociationIdAndIsActiveTrueOrderByUpdatedAtDesc(
            java.util.UUID organizationId, java.util.UUID associationId);
}
