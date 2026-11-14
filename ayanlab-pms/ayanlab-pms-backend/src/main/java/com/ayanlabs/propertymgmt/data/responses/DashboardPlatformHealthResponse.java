package com.ayanlabs.propertymgmt.data.responses;

public record DashboardPlatformHealthResponse(
        String status,
        long activeOrgs,
        long incidents,
        String uptimeNote
) {
}
