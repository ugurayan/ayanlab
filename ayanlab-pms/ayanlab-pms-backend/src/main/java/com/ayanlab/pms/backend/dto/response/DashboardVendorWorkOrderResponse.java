package com.ayanlab.pms.backend.dto.response;

import java.util.UUID;

public record DashboardVendorWorkOrderResponse(
        UUID id,
        String issue,
        String propertyLabel,
        String status,
        String priority
) {
}
