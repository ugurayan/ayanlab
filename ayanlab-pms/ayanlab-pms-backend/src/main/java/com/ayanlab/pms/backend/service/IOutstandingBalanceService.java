package com.ayanlab.pms.backend.service;

import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

import java.util.UUID;

import com.ayanlab.pms.backend.entity.rentals.OutstandingBalance;

public interface IOutstandingBalanceService extends IIpmsOrgScopedService<OutstandingBalance> {

    List<OutstandingBalance> findAll(CoreContext context, UUID leaseId);
}
