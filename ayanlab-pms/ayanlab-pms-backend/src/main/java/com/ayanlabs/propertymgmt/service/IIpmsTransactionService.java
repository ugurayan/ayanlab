package com.ayanlabs.propertymgmt.service;

import com.ayanlabs.core.data.CoreContext;
import java.util.List;
import java.util.UUID;

import java.util.UUID;

import com.ayanlabs.propertymgmt.entity.accounting.IpmsTransaction;

public interface IIpmsTransactionService extends IIpmsOrgScopedService<IpmsTransaction> {

    List<IpmsTransaction> findAll(CoreContext context, UUID propertyId);
}
