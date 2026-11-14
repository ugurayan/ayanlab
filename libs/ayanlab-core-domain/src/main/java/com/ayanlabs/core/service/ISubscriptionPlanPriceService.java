package com.ayanlabs.core.service;

import com.ayanlabs.core.entity.SubscriptionPlanPrice;

import java.util.List;
import java.util.UUID;

public interface ISubscriptionPlanPriceService extends IBaseService<SubscriptionPlanPrice> {

    List<SubscriptionPlanPrice> listByPlan(UUID subscriptionPlanId, boolean activeOnly);
}
