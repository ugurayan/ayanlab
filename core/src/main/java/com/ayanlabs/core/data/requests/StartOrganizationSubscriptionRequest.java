package com.ayanlabs.core.data.requests;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record StartOrganizationSubscriptionRequest(
    @NotNull UUID planId,
    UUID appId
) {}
