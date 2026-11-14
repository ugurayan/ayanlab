package com.ayanlabs.core.repository;

import com.ayanlabs.core.entity.SubscriptionPlanFeature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SubscriptionPlanFeatureRepository extends JpaRepository<SubscriptionPlanFeature, UUID> {

    List<SubscriptionPlanFeature> findBySubscriptionPlanIdAndIsActiveTrueOrderBySortOrderAscTitleAsc(
        UUID subscriptionPlanId
    );

    List<SubscriptionPlanFeature> findBySubscriptionPlanIdOrderBySortOrderAscTitleAsc(UUID subscriptionPlanId);
}
