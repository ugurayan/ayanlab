package com.ayanlab.pms.backend.data.responses;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** PM home dashboard widgets (Buildium-style); shape aligned with PMS UI mock. */
public record DashboardPmWidgetsResponse(
        List<BalanceWidget> balances,
        List<MaintenanceWidget> maintenance,
        List<ActivityWidget> activity,
        List<InsuranceGapWidget> insuranceGaps,
        List<ExpiringLeaseWidget> expiringLeases,
        ListingStatsWidget listings,
        List<ViolationWidget> violations,
        long associationDelinquentCents,
        List<BankUnmatchedWidget> bankUnmatched,
        int openTasks
) {
    public record BalanceWidget(
            UUID id,
            String propertyName,
            String partyName,
            String partyType,
            long amountCents,
            int daysPastDue
    ) {}

    public record MaintenanceWidget(
            UUID id,
            String propertyName,
            String summary,
            String status,
            Instant createdAt
    ) {}

    public record ActivityWidget(UUID id, String summary, Instant createdAt) {}

    public record InsuranceGapWidget(String tenantName, String status) {}

    public record ExpiringLeaseWidget(UUID id, String status, String endDate) {}

    public record ListingStatsWidget(int vacant, int applicationsPending) {}

    public record ViolationWidget(
            UUID id,
            UUID associationId,
            String unitLabel,
            String status,
            long fineCents,
            String category,
            String description,
            Instant openedAt
    ) {}

    public record BankUnmatchedWidget(UUID id, String description, long amountCents) {}
}