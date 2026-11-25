package com.ayanlab.pms.backend.dto.response;

public record DashboardRevenueSummaryResponse(
        long collectedCents,
        long expectedCents,
        double percent
) {
}
