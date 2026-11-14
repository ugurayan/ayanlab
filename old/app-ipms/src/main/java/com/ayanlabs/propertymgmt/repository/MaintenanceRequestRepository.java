package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.maintenance.MaintenanceRequest;
import org.springframework.stereotype.Repository;

@Repository
public interface MaintenanceRequestRepository extends IpmsOrgRepository<MaintenanceRequest, java.util.UUID> {
    java.util.List<MaintenanceRequest> findByOrganizationIdAndPropertyIdAndIsActiveTrueOrderByUpdatedAtDesc(
            java.util.UUID organizationId, java.util.UUID propertyId);

    java.util.List<MaintenanceRequest> findByOrganizationIdAndUnitIdAndIsActiveTrueOrderByUpdatedAtDesc(
            java.util.UUID organizationId, java.util.UUID unitId);

    java.util.List<MaintenanceRequest> findByOrganizationIdAndReportedByProfileIdAndIsActiveTrueOrderByUpdatedAtDesc(
            java.util.UUID organizationId, java.util.UUID reportedByProfileId);
}
