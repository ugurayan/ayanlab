package com.ayanlabs.propertymgmt.data.responses;

public record DashboardRevenueSummaryResponse(
        long collectedCents,
        long expectedCents,
        double percent
) {
}
