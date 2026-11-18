package com.ayanlab.pms.backend.data.responses;

import java.time.Instant;
import java.util.UUID;

public record DashboardTenantMaintenanceItemResponse(
        UUID id,
        String title,
        String status,
        Instant updatedAt
) {
}
