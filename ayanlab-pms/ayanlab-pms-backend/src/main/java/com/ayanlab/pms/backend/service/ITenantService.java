package com.ayanlab.pms.backend.service;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.pms.backend.entity.leasing.Tenant;

import java.util.List;
import java.util.UUID;

public interface ITenantService extends IIpmsOrgScopedService<Tenant> {

    List<Tenant> findAll(CoreContext context, UUID propertyId, UUID unitId, UUID leaseId);
}
