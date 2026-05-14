package com.ayanlabs.core.service.impl;

import com.ayanlabs.core.data.dto.SubscriptionDtos;
import com.ayanlabs.core.data.enums.BillingPeriodEnum;
import com.ayanlabs.core.data.enums.SubscriptionStatusEnum;
import com.ayanlabs.core.data.requests.StartOrganizationSubscriptionRequest;
import com.ayanlabs.core.data.requests.StartUserSubscriptionRequest;
import com.ayanlabs.core.entity.OrganizationSubscription;
import com.ayanlabs.core.entity.SubscriptionPlan;
import com.ayanlabs.core.entity.UserSubscription;
import com.ayanlabs.core.exception.CoreException;
import com.ayanlabs.core.repository.OrganizationSubscriptionRepository;
import com.ayanlabs.core.repository.SubscriptionPlanRepository;
import com.ayanlabs.core.repository.UserSubscriptionRepository;
import com.ayanlabs.core.service.ISubscriptionService;
import com.ayanlabs.core.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionService implements ISubscriptionService {

    private final SubscriptionPlanRepository planRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final OrganizationSubscriptionRepository organizationSubscriptionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionDtos.SubscriptionPlanView> listActivePlans() {
        return planRepository.findByCatalogActiveTrueOrderByNameAsc().stream()
            .map(SubscriptionDtos.SubscriptionPlanView::from)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionDtos.UserSubscriptionView> listUserSubscriptions(UUID userId, UUID appId) {
        List<UserSubscription> rows = appId == null
            ? userSubscriptionRepository.findByUserIdOrderByStartedAtDesc(userId)
            : userSubscriptionRepository.findByUserIdAndAppIdOrderByStartedAtDesc(userId, appId);
        return rows.stream().map(SubscriptionDtos.UserSubscriptionView::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionDtos.OrganizationSubscriptionView> listOrganizationSubscriptions(UUID organizationId) {
        return organizationSubscriptionRepository.findByOrganizationIdOrderByStartedAtDesc(organizationId).stream()
            .map(SubscriptionDtos.OrganizationSubscriptionView::from)
            .toList();
    }

    @Override
    @Transactional
    public SubscriptionDtos.UserSubscriptionView startUserSubscription(UUID userId, StartUserSubscriptionRequest request) {
        SubscriptionPlan plan = planRepository.findById(request.planId())
            .orElseThrow(() -> new CoreException("NOT_FOUND", "Subscription plan not found"));
        if (!Boolean.TRUE.equals(plan.getCatalogActive())) {
            throw new CoreException("INVALID_STATE", "Plan is not available for purchase");
        }
        Instant now = Instant.now();
        UserSubscription row = UserSubscription.builder()
            .userId(userId)
            .appId(request.appId())
            .subscriptionPlanId(plan.getId())
            .status(SubscriptionStatusEnum.ACTIVE)
            .startedAt(now)
            .currentPeriodEnd(periodEnd(plan.getBillingPeriod(), now))
            .build();
        IdGenerator.assignIfAbsent(row);
        userSubscriptionRepository.save(row);
        return SubscriptionDtos.UserSubscriptionView.from(row);
    }

    @Override
    @Transactional
    public SubscriptionDtos.OrganizationSubscriptionView startOrganizationSubscription(UUID organizationId, StartOrganizationSubscriptionRequest request) {
        SubscriptionPlan plan = planRepository.findById(request.planId())
            .orElseThrow(() -> new CoreException("NOT_FOUND", "Subscription plan not found"));
        if (!Boolean.TRUE.equals(plan.getCatalogActive())) {
            throw new CoreException("INVALID_STATE", "Plan is not available for purchase");
        }
        Instant now = Instant.now();
        OrganizationSubscription row = OrganizationSubscription.builder()
            .organizationId(organizationId)
            .appId(request.appId())
            .subscriptionPlanId(plan.getId())
            .status(SubscriptionStatusEnum.ACTIVE)
            .startedAt(now)
            .currentPeriodEnd(periodEnd(plan.getBillingPeriod(), now))
            .build();
        IdGenerator.assignIfAbsent(row);
        organizationSubscriptionRepository.save(row);
        return SubscriptionDtos.OrganizationSubscriptionView.from(row);
    }

    private static Instant periodEnd(BillingPeriodEnum period, Instant start) {
        if (period == null) {
            return null;
        }
        return switch (period) {
            case MONTHLY -> start.plus(30, ChronoUnit.DAYS);
            case YEARLY -> start.plus(365, ChronoUnit.DAYS);
            case LIFETIME -> null;
        };
    }
}
