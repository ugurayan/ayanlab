package com.ayanlabs.propertymgmt.data.responses;

public record DashboardPlatformUsersResponse(
        long total,
        long activeToday,
        long newThisWeek
) {
}
