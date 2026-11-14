package com.ayanlabs.core.data.requests;

import com.ayanlabs.core.data.enums.BillingPeriodEnum;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record StartProfileSubscriptionRequest(
    @NotNull UUID profileId,
    @NotNull UUID planId,
    UUID planPriceId,
    BillingPeriodEnum billingPeriod
) {}
