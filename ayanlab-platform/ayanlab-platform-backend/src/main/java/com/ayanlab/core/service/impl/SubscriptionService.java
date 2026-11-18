package com.ayanlab.core.service.impl;

import com.ayanlab.core.data.dto.SubscriptionDtos;
import com.ayanlab.core.data.enums.BillingPeriodEnum;
import com.ayanlab.core.data.requests.StartOrganizationSubscriptionRequest;
import com.ayanlab.core.data.requests.StartProfileSubscriptionRequest;
import com.ayanlab.core.data.requests.StartUserSubscriptionRequest;
import com.ayanlab.core.entity.OrganizationSubscription;
import com.ayanlab.core.entity.ProfileSubscription;
import com.ayanlab.core.entity.SubscriptionPlan;
import com.ayanlab.core.entity.SubscriptionPlanFeature;
import com.ayanlab.core.entity.SubscriptionPlanPrice;
import com.ayanlab.core.entity.UserProfile;
import com.ayanlab.core.exception.CoreException;
import com.ayanlab.core.repository.OrganizationSubscriptionRepository;
import com.ayanlab.core.repository.SubscriptionPlanFeatureRepository;
import com.ayanlab.core.repository.SubscriptionPlanPriceRepository;
import com.ayanlab.core.repository.SubscriptionPlanRepository;
import com.ayanlab.core.repository.UserProfileRepository;
import com.ayanlab.core.service.IProfileSubscriptionService;
import com.ayanlab.core.service.ISubscriptionService;
import com.ayanlab.core.service.support.SubscriptionBillingSupport;
import com.ayanlab.core.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionService implements ISubscriptionService {

    private final SubscriptionPlanRepository planRepository;
    private final SubscriptionPlanPriceRepository priceRepository;
    private final SubscriptionPlanFeatureRepository featureRepository;
    private final UserProfileRepository userProfileRepository;
    private final IProfileSubscriptionService profileSubscriptionService;
    private final OrganizationSubscriptionRepository organizationSubscriptionRepository;
    private final SubscriptionBillingSupport billingSupport;

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionDtos.SubscriptionPlanView> listCatalog(UUID appId) {
        if (appId == null) {
            throw new CoreException("INVALID_ARGUMENT", "appId is required");
        }
        return planRepository.findByAppIdAndCatalogActiveTrueOrderBySortOrderAscNameAsc(appId).stream()
            .map(this::toPlanView)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionDtos.ProfileSubscriptionView> listProfileSubscriptions(UUID profileId) {
        return profileSubscriptionService.listByProfile(profileId).stream()
            .map(SubscriptionDtos.ProfileSubscriptionView::from)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionDtos.UserSubscriptionView> listUserSubscriptions(UUID userId, UUID appId) {
        UserProfile profile = resolveProfile(userId, appId);
        return profileSubscriptionService.listByProfile(profile.getId()).stream()
            .map(row -> SubscriptionDtos.UserSubscriptionView.from(row, userId, profile.getAppId()))
            .toList();
    }

    @Override
    @Transactional
    public SubscriptionDtos.UserSubscriptionView startUserSubscription(UUID userId, StartUserSubscriptionRequest request) {
        UUID appId = request.appId();
        UserProfile profile = resolveProfile(userId, appId);
        SubscriptionDtos.ProfileSubscriptionView started = profileSubscriptionService.start(
            new StartProfileSubscriptionRequest(
                profile.getId(),
                request.planId(),
                request.planPriceId(),
                request.billingPeriod()
            )
        );
        return new SubscriptionDtos.UserSubscriptionView(
            started.id(),
            userId,
            profile.getAppId(),
            started.profileId(),
            started.subscriptionPlanId(),
            started.subscriptionPlanPriceId(),
            started.status(),
            started.startedAt(),
            started.currentPeriodEnd(),
            started.cancelledAt()
        );
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
    public SubscriptionDtos.OrganizationSubscriptionView startOrganizationSubscription(
        UUID organizationId,
        StartOrganizationSubscriptionRequest request
    ) {
        SubscriptionPlan plan = planRepository.findById(request.planId())
            .orElseThrow(() -> new CoreException("NOT_FOUND", "Subscription plan not found"));
        billingSupport.assertPlanPurchasable(plan);

        var price = billingSupport.resolvePrice(plan, request);
        Instant now = Instant.now();
        BillingPeriodEnum billingPeriod = price != null ? price.getBillingPeriod() : BillingPeriodEnum.MONTHLY;

        OrganizationSubscription row = OrganizationSubscription.builder()
            .organizationId(organizationId)
            .appId(request.appId() != null ? request.appId() : plan.getAppId())
            .subscriptionPlanId(plan.getId())
            .subscriptionPlanPriceId(price != null ? price.getId() : null)
            .status(billingSupport.initialStatus(plan, now))
            .startedAt(now)
            .currentPeriodStart(now)
            .currentPeriodEnd(billingSupport.periodEnd(billingPeriod, now))
            .trialEndsAt(billingSupport.trialEndsAt(plan, now))
            .autoRenew(!Boolean.TRUE.equals(plan.getIsFree()))
            .build();
        IdGenerator.assignIfAbsent(row);
        organizationSubscriptionRepository.save(row);
        return SubscriptionDtos.OrganizationSubscriptionView.from(row);
    }

    private SubscriptionDtos.SubscriptionPlanView toPlanView(SubscriptionPlan plan) {
        List<SubscriptionDtos.SubscriptionPlanPriceView> prices = priceRepository
            .findBySubscriptionPlanIdAndCatalogActiveTrueOrderByBillingPeriodAsc(plan.getId())
            .stream()
            .map(SubscriptionDtos.SubscriptionPlanPriceView::from)
            .toList();
        List<SubscriptionDtos.SubscriptionPlanFeatureView> features = featureRepository
            .findBySubscriptionPlanIdAndIsActiveTrueOrderBySortOrderAscTitleAsc(plan.getId())
            .stream()
            .map(SubscriptionDtos.SubscriptionPlanFeatureView::from)
            .toList();
        return SubscriptionDtos.SubscriptionPlanView.from(plan, prices, features);
    }

    private UserProfile resolveProfile(UUID userId, UUID appId) {
        if (appId == null) {
            throw new CoreException("INVALID_ARGUMENT", "appId is required");
        }
        return userProfileRepository.findByUserIdAndAppId(userId, appId)
            .orElseThrow(() -> new CoreException("NOT_FOUND", "User profile not found for app"));
    }
}
