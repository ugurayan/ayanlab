package com.ayanlab.pms.backend.data.responses;

public record DashboardPlatformUsersResponse(
        long total,
        long activeToday,
        long newThisWeek
) {
}
