package com.ayanlabs.core.repository;

import com.ayanlabs.core.data.enums.SubscriptionStatusEnum;
import com.ayanlabs.core.entity.ProfileSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfileSubscriptionRepository extends JpaRepository<ProfileSubscription, UUID> {

    List<ProfileSubscription> findByProfileIdOrderByStartedAtDesc(UUID profileId);

    List<ProfileSubscription> findByProfileIdAndStatusOrderByStartedAtDesc(
        UUID profileId,
        SubscriptionStatusEnum status
    );

    Optional<ProfileSubscription> findFirstByProfileIdAndStatusInOrderByStartedAtDesc(
        UUID profileId,
        List<SubscriptionStatusEnum> statuses
    );
}
