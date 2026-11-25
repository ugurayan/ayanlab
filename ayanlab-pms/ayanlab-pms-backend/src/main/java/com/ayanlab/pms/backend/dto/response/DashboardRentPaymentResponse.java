package com.ayanlab.pms.backend.dto.response;

import java.time.Instant;

public record DashboardRentPaymentResponse(
        long balanceDueCents,
        Long lastPaymentAmountCents,
        Instant lastPaymentAt,
        boolean autopay
) {
}
