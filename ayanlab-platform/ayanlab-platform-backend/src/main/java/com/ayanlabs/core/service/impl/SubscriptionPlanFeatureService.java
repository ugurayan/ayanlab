package com.ayanlabs.core.service.impl;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.enums.ActionEnum;
import com.ayanlabs.core.entity.SubscriptionPlan;
import com.ayanlabs.core.entity.SubscriptionPlanFeature;
import com.ayanlabs.core.exception.CoreException;
import com.ayanlabs.core.repository.SubscriptionPlanFeatureRepository;
import com.ayanlabs.core.repository.SubscriptionPlanRepository;
import com.ayanlabs.core.service.ISubscriptionPlanFeatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.ayanlabs.core.data.mapper.BaseMapper.map;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanFeatureService implements ISubscriptionPlanFeatureService {

    private final SubscriptionPlanFeatureRepository featureRepository;
    private final SubscriptionPlanRepository planRepository;

    @Override
    @Transactional
    public SubscriptionPlanFeature create(SubscriptionPlanFeature item, CoreContext context) {
        assertPlanAccessible(item.getSubscriptionPlanId(), context);
        item = map(item, context.getUserId(), ActionEnum.CREATE);
        return featureRepository.save(item);
    }

    @Override
    @Transactional
    public SubscriptionPlanFeature update(SubscriptionPlanFeature item, CoreContext context) {
        if (item.getId() == null) {
            throw new CoreException("INVALID_ARGUMENT", "SubscriptionPlanFeature id required");
        }
        SubscriptionPlanFeature current = featureRepository.findById(item.getId())
            .orElseThrow(() -> new CoreException("NOT_FOUND", "Plan feature not found"));
        assertPlanAccessible(current.getSubscriptionPlanId(), context);
        if (item.getSubscriptionPlanId() == null) {
            item.setSubscriptionPlanId(current.getSubscriptionPlanId());
        }
        item = map(item, context.getUserId(), ActionEnum.UPDATE);
        return featureRepository.save(item);
    }

    @Override
    @Transactional
    public void delete(SubscriptionPlanFeature item, CoreContext context) {
        item.setIsActive(false);
        item = map(item, context.getUserId(), ActionEnum.DELETE);
        featureRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        SubscriptionPlanFeature feature = findById(id, context);
        if (feature != null) {
            delete(feature, context);
        }
    }

    @Override
    public SubscriptionPlanFeature findById(UUID id, CoreContext context) {
        SubscriptionPlanFeature feature = featureRepository.findById(id).orElse(null);
        if (feature == null) {
            return null;
        }
        assertPlanAccessible(feature.getSubscriptionPlanId(), context);
        return feature;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionPlanFeature> listByPlan(UUID subscriptionPlanId) {
        return featureRepository.findBySubscriptionPlanIdAndIsActiveTrueOrderBySortOrderAscTitleAsc(subscriptionPlanId);
    }

    private void assertPlanAccessible(UUID planId, CoreContext context) {
        SubscriptionPlan plan = planRepository.findById(planId)
            .orElseThrow(() -> new CoreException("NOT_FOUND", "Subscription plan not found"));
        if (context.getAppId() != null && !context.getAppId().equals(plan.getAppId())) {
            throw new CoreException("FORBIDDEN", "Plan does not belong to the current app");
        }
    }

    @Override
    @Transactional
    public void hardDeleteById(UUID id, CoreContext context) {
        SubscriptionPlanFeature found = findById(id, context);
        if (found != null) {
            featureRepository.delete(found);
        }
    }

    @Override
    @Transactional
    public SubscriptionPlanFeature setActive(UUID id, boolean active, CoreContext context) {
        SubscriptionPlanFeature found = findById(id, context);
        if (found == null) {
            throw new CoreException("NOT_FOUND", "SubscriptionPlanFeature not found");
        }
        found.setIsActive(active);
        found = map(found, context.getUserId(), ActionEnum.UPDATE);
        found.setIsActive(active);
        return featureRepository.save(found);
    }
}
