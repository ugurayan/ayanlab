package com.ayanlab.pms.backend.data.responses;

public record DashboardVendorPerformanceResponse(
        double onTimePercent,
        double avgResponseHrs,
        long completedWeek
) {
}
