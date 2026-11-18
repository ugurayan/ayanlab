package com.ayanlab.core.service;

import com.ayanlab.core.data.dto.SubscriptionDtos;
import com.ayanlab.core.data.requests.StartProfileSubscriptionRequest;
import com.ayanlab.core.entity.ProfileSubscription;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IProfileSubscriptionService extends IBaseService<ProfileSubscription> {

    List<ProfileSubscription> listByProfile(UUID profileId);

    Optional<ProfileSubscription> findActiveForProfile(UUID profileId);

    SubscriptionDtos.ProfileSubscriptionView start(StartProfileSubscriptionRequest request);
}
