package com.ayanlab.core.data.requests;

import com.ayanlab.core.data.enums.BillingPeriodEnum;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record StartOrganizationSubscriptionRequest(
    @NotNull UUID planId,
    UUID appId,
    UUID planPriceId,
    BillingPeriodEnum billingPeriod
) {}
