package com.ayanlab.core.dto.request;

import com.ayanlab.core.enums.BillingPeriodEnum;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record StartOrganizationSubscriptionRequest(
    @NotNull UUID planId,
    UUID appId,
    UUID planPriceId,
    BillingPeriodEnum billingPeriod
) {}
