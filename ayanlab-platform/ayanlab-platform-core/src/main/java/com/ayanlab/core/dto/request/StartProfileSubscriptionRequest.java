package com.ayanlab.core.dto.request;

import com.ayanlab.core.enums.BillingPeriodEnum;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record StartProfileSubscriptionRequest(
    @NotNull UUID profileId,
    @NotNull UUID planId,
    UUID planPriceId,
    BillingPeriodEnum billingPeriod
) {}
