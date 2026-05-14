package com.ayanlabs.core.data.requests;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record StartUserSubscriptionRequest(
    @NotNull UUID planId,
    UUID appId
) {}
