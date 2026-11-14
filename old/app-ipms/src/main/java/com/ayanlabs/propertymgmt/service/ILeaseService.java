package com.ayanlabs.propertymgmt.service;

import com.ayanlabs.core.data.CoreContext;
import java.util.List;
import java.util.UUID;

import java.util.UUID;

import com.ayanlabs.propertymgmt.entity.leasing.Lease;

public interface ILeaseService extends IIpmsOrgScopedService<Lease> {

    List<Lease> findAll(CoreContext context, UUID propertyId, UUID unitId);
}
