package com.ayanlab.pms.backend.data.responses;

public record DashboardPlatformHealthResponse(
        String status,
        long activeOrgs,
        long incidents,
        String uptimeNote
) {
}
