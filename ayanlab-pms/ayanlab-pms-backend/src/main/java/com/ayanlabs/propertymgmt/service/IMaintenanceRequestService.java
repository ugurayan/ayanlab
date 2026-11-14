package com.ayanlabs.propertymgmt.service;

import com.ayanlabs.core.data.CoreContext;
import java.util.List;
import java.util.UUID;

import java.util.UUID;

import com.ayanlabs.propertymgmt.entity.maintenance.MaintenanceRequest;

public interface IMaintenanceRequestService extends IIpmsOrgScopedService<MaintenanceRequest> {

    List<MaintenanceRequest> findAll(CoreContext context, UUID propertyId, UUID unitId);
}
