package com.ayanlab.pms.backend.service;

import com.ayanlab.core.data.CoreContext;
import java.util.List;
import java.util.UUID;

import java.util.UUID;

import com.ayanlab.pms.backend.entity.accounting.Bill;

public interface IBillService extends IIpmsOrgScopedService<Bill> {

    List<Bill> findAll(CoreContext context, UUID propertyId, UUID vendorId);
}
