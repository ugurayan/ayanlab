package com.ayanlabs.core.repository;

import com.ayanlabs.core.entity.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, UUID> {
    List<UserSubscription> findByUserIdOrderByStartedAtDesc(UUID userId);

    List<UserSubscription> findByUserIdAndAppIdOrderByStartedAtDesc(UUID userId, UUID appId);
}
