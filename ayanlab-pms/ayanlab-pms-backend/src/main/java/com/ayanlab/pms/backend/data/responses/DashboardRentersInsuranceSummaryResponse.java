package com.ayanlab.pms.backend.data.responses;

public record DashboardRentersInsuranceSummaryResponse(
        long total,
        long insured,
        long uninsured,
        long expiringSoon
) {
}
