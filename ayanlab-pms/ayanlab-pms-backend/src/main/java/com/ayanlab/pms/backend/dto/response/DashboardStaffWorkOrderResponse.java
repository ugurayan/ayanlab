package com.ayanlab.pms.backend.dto.response;

import java.time.Instant;
import java.util.UUID;

public record DashboardStaffWorkOrderResponse(
        UUID id,
        String issue,
        String propertyLabel,
        String status,
        Instant scheduledAt,
        String timeLabel
) {
}
