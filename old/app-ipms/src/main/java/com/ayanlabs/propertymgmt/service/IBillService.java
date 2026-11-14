package com.ayanlabs.propertymgmt.service;

import com.ayanlabs.core.data.CoreContext;
import java.util.List;
import java.util.UUID;

import java.util.UUID;

import com.ayanlabs.propertymgmt.entity.accounting.Bill;

public interface IBillService extends IIpmsOrgScopedService<Bill> {

    List<Bill> findAll(CoreContext context, UUID propertyId, UUID vendorId);
}
