package com.ayanlab.pms.backend.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public record DashboardTenantInsuranceResponse(
        UUID policyId,
        String status,
        boolean required,
        LocalDate deadline,
        String carrierName,
        String policyNumber,
        LocalDate expirationDate
) {
}
