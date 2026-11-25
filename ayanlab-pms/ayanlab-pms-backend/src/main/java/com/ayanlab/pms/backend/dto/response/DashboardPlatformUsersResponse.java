package com.ayanlab.pms.backend.dto.response;

public record DashboardPlatformUsersResponse(
        long total,
        long activeToday,
        long newThisWeek
) {
}
