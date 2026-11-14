package com.ayanlabs.propertymgmt.data.responses;

public record DashboardRentersInsuranceSummaryResponse(
        long total,
        long insured,
        long uninsured,
        long expiringSoon
) {
}
