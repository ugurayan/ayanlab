package com.ayanlab.pms.backend.data.responses;

import java.time.LocalDate;
import java.util.UUID;

public record DashboardMyLeaseResponse(
        UUID leaseId,
        String unitLabel,
        long monthlyRentCents,
        LocalDate leaseEnd,
        Integer dueDay,
        String manager
) {
}
