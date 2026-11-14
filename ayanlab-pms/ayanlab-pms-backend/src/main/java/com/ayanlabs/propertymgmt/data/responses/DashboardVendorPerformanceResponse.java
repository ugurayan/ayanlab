package com.ayanlabs.propertymgmt.data.responses;

public record DashboardVendorPerformanceResponse(
        double onTimePercent,
        double avgResponseHrs,
        long completedWeek
) {
}
