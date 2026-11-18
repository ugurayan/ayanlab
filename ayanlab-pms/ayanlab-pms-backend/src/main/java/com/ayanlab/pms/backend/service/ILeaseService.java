package com.ayanlab.pms.backend.service;

import com.ayanlab.core.data.CoreContext;
import java.util.List;
import java.util.UUID;

import java.util.UUID;

import com.ayanlab.pms.backend.entity.leasing.Lease;

public interface ILeaseService extends IIpmsOrgScopedService<Lease> {

    List<Lease> findAll(CoreContext context, UUID propertyId, UUID unitId);
}
