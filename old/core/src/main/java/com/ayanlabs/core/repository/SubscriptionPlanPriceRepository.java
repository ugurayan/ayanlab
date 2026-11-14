package com.ayanlabs.core.repository;

import com.ayanlabs.core.data.enums.BillingPeriodEnum;
import com.ayanlabs.core.entity.SubscriptionPlanPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionPlanPriceRepository extends JpaRepository<SubscriptionPlanPrice, UUID> {

    List<SubscriptionPlanPrice> findBySubscriptionPlanIdAndCatalogActiveTrueOrderByBillingPeriodAsc(
        UUID subscriptionPlanId
    );

    List<SubscriptionPlanPrice> findBySubscriptionPlanIdOrderByBillingPeriodAsc(UUID subscriptionPlanId);

    Optional<SubscriptionPlanPrice> findBySubscriptionPlanIdAndBillingPeriod(
        UUID subscriptionPlanId,
        BillingPeriodEnum billingPeriod
    );
}
