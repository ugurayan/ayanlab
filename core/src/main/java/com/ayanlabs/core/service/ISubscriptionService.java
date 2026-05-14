package com.ayanlabs.core.service;

import com.ayanlabs.core.data.dto.SubscriptionDtos;
import com.ayanlabs.core.data.requests.StartOrganizationSubscriptionRequest;
import com.ayanlabs.core.data.requests.StartUserSubscriptionRequest;

import java.util.List;
import java.util.UUID;

public interface ISubscriptionService {

    List<SubscriptionDtos.SubscriptionPlanView> listActivePlans();

    List<SubscriptionDtos.UserSubscriptionView> listUserSubscriptions(UUID userId, UUID appId);

    List<SubscriptionDtos.OrganizationSubscriptionView> listOrganizationSubscriptions(UUID organizationId);

    SubscriptionDtos.UserSubscriptionView startUserSubscription(UUID userId, StartUserSubscriptionRequest request);

    SubscriptionDtos.OrganizationSubscriptionView startOrganizationSubscription(UUID organizationId, StartOrganizationSubscriptionRequest request);
}
