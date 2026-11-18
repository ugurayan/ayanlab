package com.ayanlab.pms.backend.service;

import com.ayanlab.core.data.CoreContext;
import java.util.List;
import java.util.UUID;

import java.util.UUID;

import com.ayanlab.pms.backend.entity.maintenance.MaintenanceRequest;

public interface IMaintenanceRequestService extends IIpmsOrgScopedService<MaintenanceRequest> {

    List<MaintenanceRequest> findAll(CoreContext context, UUID propertyId, UUID unitId);
}
