package com.ayanlabs.propertymgmt.service;

import com.ayanlabs.core.data.CoreContext;
import java.util.List;
import java.util.UUID;

import java.util.UUID;

import com.ayanlabs.propertymgmt.entity.leasing.DraftLease;

public interface IDraftLeaseService extends IIpmsOrgScopedService<DraftLease> {

    List<DraftLease> findAll(CoreContext context, UUID propertyId, UUID unitId);
}
