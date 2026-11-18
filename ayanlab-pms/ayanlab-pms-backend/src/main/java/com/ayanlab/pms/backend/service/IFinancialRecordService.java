package com.ayanlab.pms.backend.service;

import com.ayanlab.core.data.CoreContext;
import java.util.List;
import java.util.UUID;

import java.util.UUID;

import com.ayanlab.pms.backend.entity.accounting.FinancialRecord;

public interface IFinancialRecordService extends IIpmsOrgScopedService<FinancialRecord> {

    List<FinancialRecord> findAll(CoreContext context, UUID propertyId);
}
