package com.ayanlabs.propertymgmt.service;

import com.ayanlabs.core.data.CoreContext;
import java.util.List;
import java.util.UUID;

import java.util.UUID;

import com.ayanlabs.propertymgmt.entity.leasing.LeaseRenewal;

public interface ILeaseRenewalService extends IIpmsOrgScopedService<LeaseRenewal> {

    List<LeaseRenewal> findAll(CoreContext context, UUID leaseId);
}
