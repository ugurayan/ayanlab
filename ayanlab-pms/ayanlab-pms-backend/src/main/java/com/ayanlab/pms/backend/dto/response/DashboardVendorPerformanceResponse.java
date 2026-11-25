package com.ayanlab.pms.backend.dto.response;

public record DashboardVendorPerformanceResponse(
        double onTimePercent,
        double avgResponseHrs,
        long completedWeek
) {
}
