package com.ayanlabs.core.repository;

import com.ayanlabs.core.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, UUID> {

    Optional<SubscriptionPlan> findByAppIdAndCode(UUID appId, String code);

    List<SubscriptionPlan> findByAppIdAndCatalogActiveTrueOrderBySortOrderAscNameAsc(UUID appId);

    List<SubscriptionPlan> findByAppIdOrderBySortOrderAscNameAsc(UUID appId);
}
