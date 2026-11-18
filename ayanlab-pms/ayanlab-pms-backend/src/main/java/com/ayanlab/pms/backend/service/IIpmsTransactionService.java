package com.ayanlab.pms.backend.service;

import com.ayanlab.core.data.CoreContext;
import java.util.List;
import java.util.UUID;

import java.util.UUID;

import com.ayanlab.pms.backend.entity.accounting.IpmsTransaction;

public interface IIpmsTransactionService extends IIpmsOrgScopedService<IpmsTransaction> {

    List<IpmsTransaction> findAll(CoreContext context, UUID propertyId);
}
