package com.ayanlabs.core.service.impl;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.dto.SubscriptionDtos;
import com.ayanlabs.core.data.enums.ActionEnum;
import com.ayanlabs.core.data.enums.BillingPeriodEnum;
import com.ayanlabs.core.data.requests.StartProfileSubscriptionRequest;
import com.ayanlabs.core.entity.ProfileSubscription;
import com.ayanlabs.core.entity.SubscriptionPlan;
import com.ayanlabs.core.entity.SubscriptionPlanPrice;
import com.ayanlabs.core.entity.UserProfile;
import com.ayanlabs.core.exception.CoreException;
import com.ayanlabs.core.repository.ProfileSubscriptionRepository;
import com.ayanlabs.core.repository.SubscriptionPlanRepository;
import com.ayanlabs.core.repository.UserProfileRepository;
import com.ayanlabs.core.service.IProfileSubscriptionService;
import com.ayanlabs.core.service.support.SubscriptionBillingSupport;
import com.ayanlabs.core.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.ayanlabs.core.data.mapper.BaseMapper.map;

@Service
@RequiredArgsConstructor
public class ProfileSubscriptionService implements IProfileSubscriptionService {

    private final ProfileSubscriptionRepository profileSubscriptionRepository;
    private final SubscriptionPlanRepository planRepository;
    private final UserProfileRepository userProfileRepository;
    private final SubscriptionBillingSupport billingSupport;

    @Override
    @Transactional
    public ProfileSubscription create(ProfileSubscription item, CoreContext context) {
        assertProfileAccessible(item.getProfileId(), context);
        item = map(item, context.getUserId(), ActionEnum.CREATE);
        return profileSubscriptionRepository.save(item);
    }

    @Override
    @Transactional
    public ProfileSubscription update(ProfileSubscription item, CoreContext context) {
        if (item.getId() == null) {
            throw new CoreException("INVALID_ARGUMENT", "ProfileSubscription id required");
        }
        ProfileSubscription current = profileSubscriptionRepository.findById(item.getId())
            .orElseThrow(() -> new CoreException("NOT_FOUND", "Profile subscription not found"));
        assertProfileAccessible(current.getProfileId(), context);
        item = map(item, context.getUserId(), ActionEnum.UPDATE);
        return profileSubscriptionRepository.save(item);
    }

    @Override
    @Transactional
    public void delete(ProfileSubscription item, CoreContext context) {
        item.setIsActive(false);
        item = map(item, context.getUserId(), ActionEnum.DELETE);
        profileSubscriptionRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        ProfileSubscription row = findById(id, context);
        if (row != null) {
            delete(row, context);
        }
    }

    @Override
    public ProfileSubscription findById(UUID id, CoreContext context) {
        ProfileSubscription row = profileSubscriptionRepository.findById(id).orElse(null);
        if (row == null) {
            return null;
        }
        if (context != null) {
            assertProfileAccessible(row.getProfileId(), context);
        }
        return row;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfileSubscription> listByProfile(UUID profileId) {
        return profileSubscriptionRepository.findByProfileIdOrderByStartedAtDesc(profileId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProfileSubscription> findActiveForProfile(UUID profileId) {
        return profileSubscriptionRepository.findFirstByProfileIdAndStatusInOrderByStartedAtDesc(
            profileId,
            SubscriptionBillingSupport.ACTIVE_STATUSES
        );
    }

    @Override
    @Transactional
    public SubscriptionDtos.ProfileSubscriptionView start(StartProfileSubscriptionRequest request) {
        UserProfile profile = userProfileRepository.findById(request.profileId())
            .orElseThrow(() -> new CoreException("NOT_FOUND", "Profile not found"));

        SubscriptionPlan plan = planRepository.findById(request.planId())
            .orElseThrow(() -> new CoreException("NOT_FOUND", "Subscription plan not found"));
        if (!plan.getAppId().equals(profile.getAppId())) {
            throw new CoreException("INVALID_ARGUMENT", "Plan does not belong to the profile app");
        }
        billingSupport.assertPlanPurchasable(plan);

        SubscriptionPlanPrice price = billingSupport.resolvePrice(plan, request);
        Instant now = Instant.now();
        BillingPeriodEnum billingPeriod = price != null ? price.getBillingPeriod() : BillingPeriodEnum.MONTHLY;

        ProfileSubscription row = ProfileSubscription.builder()
            .profileId(profile.getId())
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
        profileSubscriptionRepository.save(row);
        return SubscriptionDtos.ProfileSubscriptionView.from(row);
    }

    private void assertProfileAccessible(UUID profileId, CoreContext context) {
        UserProfile profile = userProfileRepository.findById(profileId)
            .orElseThrow(() -> new CoreException("NOT_FOUND", "Profile not found"));
        if (context.getAppId() != null && !context.getAppId().equals(profile.getAppId())) {
            throw new CoreException("FORBIDDEN", "Profile does not belong to the current app");
        }
    }

    @Override
    @Transactional
    public void hardDeleteById(UUID id, CoreContext context) {
        ProfileSubscription found = findById(id, context);
        if (found != null) {
            profileSubscriptionRepository.delete(found);
        }
    }

    @Override
    @Transactional
    public ProfileSubscription setActive(UUID id, boolean active, CoreContext context) {
        ProfileSubscription found = findById(id, context);
        if (found == null) {
            throw new CoreException("NOT_FOUND", "ProfileSubscription not found");
        }
        found.setIsActive(active);
        found = map(found, context.getUserId(), ActionEnum.UPDATE);
        found.setIsActive(active);
        return profileSubscriptionRepository.save(found);
    }
}
