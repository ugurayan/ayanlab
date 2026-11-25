package com.ayanlab.pms.backend.service;

import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

import java.util.UUID;

import com.ayanlab.pms.backend.entity.maintenance.WorkOrder;

public interface IWorkOrderService extends IIpmsOrgScopedService<WorkOrder> {

    List<WorkOrder> findAll(CoreContext context, UUID maintenanceRequestId);
}
