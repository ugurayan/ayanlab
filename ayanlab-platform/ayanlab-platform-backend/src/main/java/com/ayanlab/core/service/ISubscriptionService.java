package com.ayanlab.core.service;

import com.ayanlab.core.data.dto.SubscriptionDtos;
import com.ayanlab.core.data.requests.StartOrganizationSubscriptionRequest;
import com.ayanlab.core.data.requests.StartUserSubscriptionRequest;

import java.util.List;
import java.util.UUID;

/**
 * Facade used by app modules (vault, crm, …) for catalog + checkout flows.
 */
public interface ISubscriptionService {

    List<SubscriptionDtos.SubscriptionPlanView> listCatalog(UUID appId);

    List<SubscriptionDtos.ProfileSubscriptionView> listProfileSubscriptions(UUID profileId);

    List<SubscriptionDtos.UserSubscriptionView> listUserSubscriptions(UUID userId, UUID appId);

    SubscriptionDtos.UserSubscriptionView startUserSubscription(UUID userId, StartUserSubscriptionRequest request);

    List<SubscriptionDtos.OrganizationSubscriptionView> listOrganizationSubscriptions(UUID organizationId);

    SubscriptionDtos.OrganizationSubscriptionView startOrganizationSubscription(
        UUID organizationId,
        StartOrganizationSubscriptionRequest request
    );
}
