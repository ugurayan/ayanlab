package com.ayanlab.core.service;

import com.ayanlab.core.entity.SubscriptionPlanFeature;

import java.util.List;
import java.util.UUID;

public interface ISubscriptionPlanFeatureService extends IBaseService<SubscriptionPlanFeature> {

    List<SubscriptionPlanFeature> listByPlan(UUID subscriptionPlanId);
}
