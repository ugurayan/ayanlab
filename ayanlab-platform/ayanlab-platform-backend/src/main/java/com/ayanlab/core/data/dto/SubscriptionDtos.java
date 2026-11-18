package com.ayanlab.core.data.dto;

import com.ayanlab.core.data.enums.BillingPeriodEnum;
import com.ayanlab.core.data.enums.SubscriptionPricingUnitEnum;
import com.ayanlab.core.data.enums.SubscriptionStatusEnum;
import com.ayanlab.core.entity.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class SubscriptionDtos {

    private SubscriptionDtos() {}

    public record SubscriptionPlanFeatureView(
        UUID id,
        String featureCode,
        String title,
        String description,
        Integer sortOrder,
        Boolean included
    ) {
        public static SubscriptionPlanFeatureView from(SubscriptionPlanFeature f) {
            return new SubscriptionPlanFeatureView(
                f.getId(),
                f.getFeatureCode(),
                f.getTitle(),
                f.getDescription(),
                f.getSortOrder(),
                f.getIncluded()
            );
        }
    }

    public record SubscriptionPlanPriceView(
        UUID id,
        UUID subscriptionPlanId,
        BillingPeriodEnum billingPeriod,
        Long amountCents,
        Long listAmountCents,
        String currency,
        String appleStoreProductId,
        String googlePlayProductId,
        Boolean catalogActive
    ) {
        public static SubscriptionPlanPriceView from(SubscriptionPlanPrice p) {
            return new SubscriptionPlanPriceView(
                p.getId(),
                p.getSubscriptionPlanId(),
                p.getBillingPeriod(),
                p.getAmountCents(),
                p.getListAmountCents(),
                p.getCurrency(),
                p.getAppleStoreProductId(),
                p.getGooglePlayProductId(),
                p.getCatalogActive()
            );
        }
    }

    public record SubscriptionPlanView(
        UUID id,
        UUID appId,
        String code,
        String name,
        String description,
        Integer sortOrder,
        Boolean isFree,
        UUID inheritsFromPlanId,
        SubscriptionPricingUnitEnum pricingUnit,
        Integer maxSeats,
        Integer trialDays,
        Boolean catalogActive,
        List<SubscriptionPlanPriceView> prices,
        List<SubscriptionPlanFeatureView> features
    ) {
        public static SubscriptionPlanView from(
            SubscriptionPlan p,
            List<SubscriptionPlanPriceView> prices,
            List<SubscriptionPlanFeatureView> features
        ) {
            return new SubscriptionPlanView(
                p.getId(),
                p.getAppId(),
                p.getCode(),
                p.getName(),
                p.getDescription(),
                p.getSortOrder(),
                p.getIsFree(),
                p.getInheritsFromPlanId(),
                p.getPricingUnit(),
                p.getMaxSeats(),
                p.getTrialDays(),
                p.getCatalogActive(),
                prices,
                features
            );
        }

        /** Plan without nested children (admin CRUD). */
        public static SubscriptionPlanView from(SubscriptionPlan p) {
            return from(p, List.of(), List.of());
        }
    }

    public record ProfileSubscriptionView(
        UUID id,
        UUID profileId,
        UUID subscriptionPlanId,
        UUID subscriptionPlanPriceId,
        SubscriptionStatusEnum status,
        Instant startedAt,
        Instant currentPeriodStart,
        Instant currentPeriodEnd,
        Instant trialEndsAt,
        Instant cancelledAt,
        Boolean autoRenew,
        String externalSubscriptionId
    ) {
        public static ProfileSubscriptionView from(ProfileSubscription s) {
            return new ProfileSubscriptionView(
                s.getId(),
                s.getProfileId(),
                s.getSubscriptionPlanId(),
                s.getSubscriptionPlanPriceId(),
                s.getStatus(),
                s.getStartedAt(),
                s.getCurrentPeriodStart(),
                s.getCurrentPeriodEnd(),
                s.getTrialEndsAt(),
                s.getCancelledAt(),
                s.getAutoRenew(),
                s.getExternalSubscriptionId()
            );
        }
    }

    /**
     * Backward-compatible view for app modules that still resolve subscriptions by user + app.
     */
    public record UserSubscriptionView(
        UUID id,
        UUID userId,
        UUID appId,
        UUID profileId,
        UUID subscriptionPlanId,
        UUID subscriptionPlanPriceId,
        SubscriptionStatusEnum status,
        Instant startedAt,
        Instant currentPeriodEnd,
        Instant cancelledAt
    ) {
        public static UserSubscriptionView from(
            ProfileSubscription s,
            UUID userId,
            UUID appId
        ) {
            return new UserSubscriptionView(
                s.getId(),
                userId,
                appId,
                s.getProfileId(),
                s.getSubscriptionPlanId(),
                s.getSubscriptionPlanPriceId(),
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
        UUID subscriptionPlanPriceId,
        SubscriptionStatusEnum status,
        Instant startedAt,
        Instant currentPeriodStart,
        Instant currentPeriodEnd,
        Instant trialEndsAt,
        Instant cancelledAt,
        Boolean autoRenew,
        String externalSubscriptionId
    ) {
        public static OrganizationSubscriptionView from(OrganizationSubscription s) {
            return new OrganizationSubscriptionView(
                s.getId(),
                s.getOrganizationId(),
                s.getAppId(),
                s.getSubscriptionPlanId(),
                s.getSubscriptionPlanPriceId(),
                s.getStatus(),
                s.getStartedAt(),
                s.getCurrentPeriodStart(),
                s.getCurrentPeriodEnd(),
                s.getTrialEndsAt(),
                s.getCancelledAt(),
                s.getAutoRenew(),
                s.getExternalSubscriptionId()
            );
        }
    }
}
