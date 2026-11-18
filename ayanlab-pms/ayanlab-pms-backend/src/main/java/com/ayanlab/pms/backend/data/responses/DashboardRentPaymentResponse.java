package com.ayanlab.pms.backend.data.responses;

import java.time.Instant;

public record DashboardRentPaymentResponse(
        long balanceDueCents,
        Long lastPaymentAmountCents,
        Instant lastPaymentAt,
        boolean autopay
) {
}
