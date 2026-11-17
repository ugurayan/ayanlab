package com.ayanlabs.core.service;

import com.ayanlabs.core.entity.SubscriptionPlanFeature;

import java.util.List;
import java.util.UUID;

public interface ISubscriptionPlanFeatureService extends IBaseService<SubscriptionPlanFeature> {

    List<SubscriptionPlanFeature> listByPlan(UUID subscriptionPlanId);
}
