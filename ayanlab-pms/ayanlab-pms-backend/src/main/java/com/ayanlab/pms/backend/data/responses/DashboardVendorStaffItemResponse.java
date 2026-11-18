package com.ayanlab.pms.backend.data.responses;

import java.util.UUID;

public record DashboardVendorStaffItemResponse(
        UUID profileId,
        String name,
        String status,
        long openJobs
) {
}
