package com.ayanlab.pms.backend.dto.response;

public record DashboardPlatformHealthResponse(
        String status,
        long activeOrgs,
        long incidents,
        String uptimeNote
) {
}
