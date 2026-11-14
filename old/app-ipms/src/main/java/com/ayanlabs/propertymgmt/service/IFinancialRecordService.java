package com.ayanlabs.propertymgmt.service;

import com.ayanlabs.core.data.CoreContext;
import java.util.List;
import java.util.UUID;

import java.util.UUID;

import com.ayanlabs.propertymgmt.entity.accounting.FinancialRecord;

public interface IFinancialRecordService extends IIpmsOrgScopedService<FinancialRecord> {

    List<FinancialRecord> findAll(CoreContext context, UUID propertyId);
}
