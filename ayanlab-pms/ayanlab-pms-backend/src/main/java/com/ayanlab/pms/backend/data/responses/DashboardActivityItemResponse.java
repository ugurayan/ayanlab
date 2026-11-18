package com.ayanlab.pms.backend.data.responses;

import java.time.Instant;
import java.util.UUID;

public record DashboardActivityItemResponse(
        UUID id,
        String event,
        String detail,
        Instant occurredAt
) {
}
