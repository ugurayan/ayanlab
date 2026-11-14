package com.ayanlabs.propertymgmt.repository;

import com.ayanlabs.propertymgmt.entity.maintenance.WorkOrder;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkOrderRepository extends IpmsOrgRepository<WorkOrder, java.util.UUID> {
    java.util.List<WorkOrder> findByOrganizationIdAndMaintenanceRequestIdAndIsActiveTrueOrderByUpdatedAtDesc(
            java.util.UUID organizationId, java.util.UUID maintenanceRequestId);

    java.util.List<WorkOrder> findByOrganizationIdAndVendorIdAndIsActiveTrueOrderByUpdatedAtDesc(
            java.util.UUID organizationId, java.util.UUID vendorId);

    java.util.List<WorkOrder> findByOrganizationIdAndAssignedToProfileIdAndIsActiveTrueOrderByUpdatedAtDesc(
            java.util.UUID organizationId, java.util.UUID assignedToProfileId);
}
