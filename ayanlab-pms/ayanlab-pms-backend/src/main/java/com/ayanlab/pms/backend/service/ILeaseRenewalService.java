package com.ayanlab.pms.backend.service;

import com.ayanlab.core.data.CoreContext;
import java.util.List;
import java.util.UUID;

import java.util.UUID;

import com.ayanlab.pms.backend.entity.leasing.LeaseRenewal;

public interface ILeaseRenewalService extends IIpmsOrgScopedService<LeaseRenewal> {

    List<LeaseRenewal> findAll(CoreContext context, UUID leaseId);
}
