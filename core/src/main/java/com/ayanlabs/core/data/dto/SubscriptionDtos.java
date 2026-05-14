package com.ayanlabs.core.data.dto;

import com.ayanlabs.core.data.enums.BillingPeriodEnum;
import com.ayanlabs.core.data.enums.SubscriptionStatusEnum;
import com.ayanlabs.core.entity.OrganizationSubscription;
import com.ayanlabs.core.entity.SubscriptionPlan;
import com.ayanlabs.core.entity.UserSubscription;

import java.time.Instant;
import java.util.UUID;

public final class SubscriptionDtos {

    private SubscriptionDtos() {}

    public record SubscriptionPlanView(
        UUID id,
        String code,
        String name,
        String description,
        BillingPeriodEnum billingPeriod,
        Long amountCents,
        String currency,
        Boolean catalogActive
    ) {
        public static SubscriptionPlanView from(SubscriptionPlan p) {
            return new SubscriptionPlanView(
                p.getId(),
                p.getCode(),
                p.getName(),
                p.getDescription(),
                p.getBillingPeriod(),
                p.getAmountCents(),
                p.getCurrency(),
                p.getCatalogActive()
            );
        }
    }

    public record UserSubscriptionView(
        UUID id,
        UUID userId,
        UUID appId,
        UUID subscriptionPlanId,
        SubscriptionStatusEnum status,
        Instant startedAt,
        Instant currentPeriodEnd,
        Instant cancelledAt
    ) {
        public static UserSubscriptionView from(UserSubscription s) {
            return new UserSubscriptionView(
                s.getId(),
                s.getUserId(),
                s.getAppId(),
                s.getSubscriptionPlanId(),
                s.getStatus(),
                s.getStartedAt(),
                s.getCurrentPeriodEnd(),
                s.getCancelledAt()
            );
        }
    }

    public record OrganizationSubscriptionView(
        UUID id,
        UUID organizationId,
        UUID appId,
        UUID subscriptionPlanId,
        SubscriptionStatusEnum status,
        Instant startedAt,
        Instant currentPeriodEnd,
        Instant cancelledAt
    ) {
        public static OrganizationSubscriptionView from(OrganizationSubscription s) {
            return new OrganizationSubscriptionView(
                s.getId(),
                s.getOrganizationId(),
                s.getAppId(),
                s.getSubscriptionPlanId(),
                s.getStatus(),
                s.getStartedAt(),
                s.getCurrentPeriodEnd(),
                s.getCancelledAt()
            );
        }
    }
}
