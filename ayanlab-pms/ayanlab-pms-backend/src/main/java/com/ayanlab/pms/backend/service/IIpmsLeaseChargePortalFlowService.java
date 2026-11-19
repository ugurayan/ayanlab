package com.ayanlab.pms.backend.service;

import com.ayanlab.core.data.CoreContext;
import com.ayanlab.pms.backend.data.responses.LeaseChargePortalResult;

import java.util.UUID;

/** B2.4 — lease → rent charge → outstanding balance (tenant portal due). */
public interface IIpmsLeaseChargePortalFlowService {

    LeaseChargePortalResult execute(CoreContext context, UUID leaseId, Long chargeAmountCents);
}