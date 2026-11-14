package com.ayanlabs.propertymgmt.service;

import com.ayanlabs.core.data.CoreContext;
import java.util.List;
import java.util.UUID;

import java.util.UUID;

import com.ayanlabs.propertymgmt.entity.maintenance.WorkOrder;

public interface IWorkOrderService extends IIpmsOrgScopedService<WorkOrder> {

    List<WorkOrder> findAll(CoreContext context, UUID maintenanceRequestId);
}
