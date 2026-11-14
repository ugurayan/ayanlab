package com.ayanlabs.core.data.requests;

import com.ayanlabs.core.data.enums.BillingPeriodEnum;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Start subscription for the caller's profile in the given app.
 * Provide {@code planPriceId} or {@code billingPeriod} (resolved against the plan).
 */
public record StartUserSubscriptionRequest(
    @NotNull UUID planId,
    UUID appId,
    UUID planPriceId,
    BillingPeriodEnum billingPeriod
) {}
