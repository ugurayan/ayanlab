package com.ayanlab.pms.backend.data.responses;

import java.util.UUID;

public record LeaseChargePortalResult(
        UUID leaseId,
        UUID transactionId,
        UUID outstandingBalanceId,
        long chargeAmountCents,
        long portalBalanceDueCents,
        String status
) {}