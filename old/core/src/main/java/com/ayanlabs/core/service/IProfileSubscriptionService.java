package com.ayanlabs.core.service;

import com.ayanlabs.core.data.dto.SubscriptionDtos;
import com.ayanlabs.core.data.requests.StartProfileSubscriptionRequest;
import com.ayanlabs.core.entity.ProfileSubscription;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IProfileSubscriptionService extends IBaseService<ProfileSubscription> {

    List<ProfileSubscription> listByProfile(UUID profileId);

    Optional<ProfileSubscription> findActiveForProfile(UUID profileId);

    SubscriptionDtos.ProfileSubscriptionView start(StartProfileSubscriptionRequest request);
}
