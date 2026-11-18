package com.ayanlab.pms.backend.data.responses;

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
