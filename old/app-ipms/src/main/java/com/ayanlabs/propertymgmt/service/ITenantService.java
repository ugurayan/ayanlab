package com.ayanlabs.propertymgmt.service;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.propertymgmt.entity.leasing.Tenant;

import java.util.List;
import java.util.UUID;

public interface ITenantService extends IIpmsOrgScopedService<Tenant> {

    List<Tenant> findAll(CoreContext context, UUID propertyId, UUID unitId, UUID leaseId);
}
