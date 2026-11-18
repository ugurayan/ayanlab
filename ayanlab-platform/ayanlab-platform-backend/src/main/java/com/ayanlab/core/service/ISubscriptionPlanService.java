package com.ayanlab.core.service;

import com.ayanlab.core.entity.SubscriptionPlan;

import java.util.List;
import java.util.UUID;

public interface ISubscriptionPlanService extends IBaseService<SubscriptionPlan> {

    List<SubscriptionPlan> listByApp(UUID appId, boolean activeOnly);
}
