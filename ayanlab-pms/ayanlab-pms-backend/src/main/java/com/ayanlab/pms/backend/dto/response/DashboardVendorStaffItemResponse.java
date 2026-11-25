package com.ayanlab.pms.backend.dto.response;

import java.util.UUID;

public record DashboardVendorStaffItemResponse(
        UUID profileId,
        String name,
        String status,
        long openJobs
) {
}
