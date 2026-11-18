package com.ayanlab.pms.backend.data.responses;

public record DashboardRevenueSummaryResponse(
        long collectedCents,
        long expectedCents,
        double percent
) {
}
