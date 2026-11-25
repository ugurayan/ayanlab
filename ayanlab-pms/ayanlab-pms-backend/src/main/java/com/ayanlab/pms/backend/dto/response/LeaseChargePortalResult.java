package com.ayanlab.pms.backend.dto.response;

import java.util.UUID;

public record LeaseChargePortalResult(
        UUID leaseId,
        UUID transactionId,
        UUID outstandingBalanceId,
        long chargeAmountCents,
        long portalBalanceDueCents,
        String status
) {}