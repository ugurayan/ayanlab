package com.ayanlab.core.service.support;

import com.ayanlab.core.enums.BillingPeriodEnum;
import com.ayanlab.core.enums.SubscriptionStatusEnum;
import com.ayanlab.core.dto.request.StartOrganizationSubscriptionRequest;
import com.ayanlab.core.dto.request.StartProfileSubscriptionRequest;
import com.ayanlab.core.dto.request.StartUserSubscriptionRequest;
import com.ayanlab.core.entity.SubscriptionPlan;
import com.ayanlab.core.entity.SubscriptionPlanPrice;
import com.ayanlab.core.exception.CoreException;
import com.ayanlab.core.repository.SubscriptionPlanPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SubscriptionBillingSupport {

    public static final List<SubscriptionStatusEnum> ACTIVE_STATUSES = List.of(
        SubscriptionStatusEnum.TRIALING,
        SubscriptionStatusEnum.ACTIVE,
        SubscriptionStatusEnum.PAST_DUE
    );

    private final SubscriptionPlanPriceRepository priceRepository;

    public SubscriptionPlanPrice resolvePrice(
        SubscriptionPlan plan,
        UUID planPriceId,
        BillingPeriodEnum billingPeriod
    ) {
        if (Boolean.TRUE.equals(plan.getIsFree())) {
            return null;
        }
        if (planPriceId != null) {
            SubscriptionPlanPrice price = priceRepository.findById(planPriceId)
                .orElseThrow(() -> new CoreException("NOT_FOUND", "Plan price not found"));
            if (!plan.getId().equals(price.getSubscriptionPlanId())) {
                throw new CoreException("INVALID_ARGUMENT", "Price does not belong to the selected plan");
            }
            if (!Boolean.TRUE.equals(price.getCatalogActive())) {
                throw new CoreException("INVALID_STATE", "Price is not available");
            }
            return price;
        }
        if (billingPeriod != null) {
            return priceRepository.findBySubscriptionPlanIdAndBillingPeriod(plan.getId(), billingPeriod)
                .filter(p -> Boolean.TRUE.equals(p.getCatalogActive()))
                .orElseThrow(() -> new CoreException(
                    "NOT_FOUND",
                    "No active price for billing period " + billingPeriod
                ));
        }
        throw new CoreException("INVALID_ARGUMENT", "planPriceId or billingPeriod is required for paid plans");
    }

    public SubscriptionPlanPrice resolvePrice(SubscriptionPlan plan, StartUserSubscriptionRequest request) {
        return resolvePrice(plan, request.planPriceId(), request.billingPeriod());
    }

    public SubscriptionPlanPrice resolvePrice(SubscriptionPlan plan, StartProfileSubscriptionRequest request) {
        return resolvePrice(plan, request.planPriceId(), request.billingPeriod());
    }

    public SubscriptionPlanPrice resolvePrice(SubscriptionPlan plan, StartOrganizationSubscriptionRequest request) {
        return resolvePrice(plan, request.planPriceId(), request.billingPeriod());
    }

    public Instant periodEnd(BillingPeriodEnum period, Instant start) {
        if (period == null) {
            return null;
        }
        return switch (period) {
            case MONTHLY -> start.plus(30, ChronoUnit.DAYS);
            case YEARLY -> start.plus(365, ChronoUnit.DAYS);
            case LIFETIME -> null;
        };
    }

    public SubscriptionStatusEnum initialStatus(SubscriptionPlan plan, Instant now) {
        if (plan.getTrialDays() != null && plan.getTrialDays() > 0) {
            return SubscriptionStatusEnum.TRIALING;
        }
        return SubscriptionStatusEnum.ACTIVE;
    }

    public Instant trialEndsAt(SubscriptionPlan plan, Instant startedAt) {
        if (plan.getTrialDays() == null || plan.getTrialDays() <= 0) {
            return null;
        }
        return startedAt.plus(plan.getTrialDays(), ChronoUnit.DAYS);
    }

    public void assertPlanPurchasable(SubscriptionPlan plan) {
        if (!Boolean.TRUE.equals(plan.getCatalogActive())) {
            throw new CoreException("INVALID_STATE", "Plan is not available for purchase");
        }
        if (!Boolean.TRUE.equals(plan.getIsActive())) {
            throw new CoreException("INVALID_STATE", "Plan is inactive");
        }
    }
}
