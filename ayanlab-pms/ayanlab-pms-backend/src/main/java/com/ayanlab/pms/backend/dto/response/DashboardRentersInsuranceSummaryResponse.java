package com.ayanlab.pms.backend.dto.response;

public record DashboardRentersInsuranceSummaryResponse(
        long total,
        long insured,
        long uninsured,
        long expiringSoon
) {
}
