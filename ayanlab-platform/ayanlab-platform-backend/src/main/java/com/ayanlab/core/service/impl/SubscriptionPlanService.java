package com.ayanlab.core.service.impl;

import com.ayanlab.core.data.CoreContext;
import com.ayanlab.core.data.enums.ActionEnum;
import com.ayanlab.core.entity.SubscriptionPlan;
import com.ayanlab.core.exception.CoreException;
import com.ayanlab.core.repository.SubscriptionPlanRepository;
import com.ayanlab.core.service.ISubscriptionPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.ayanlab.core.data.mapper.BaseMapper.map;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanService implements ISubscriptionPlanService {

    private final SubscriptionPlanRepository subscriptionPlanRepository;

    @Override
    @Transactional
    public SubscriptionPlan create(SubscriptionPlan item, CoreContext context) {
        if (item.getAppId() == null) {
            item.setAppId(context.getAppId());
        }
        assertSameApp(item.getAppId(), context);
        item = map(item, context.getUserId(), ActionEnum.CREATE);
        return subscriptionPlanRepository.save(item);
    }

    @Override
    @Transactional
    public SubscriptionPlan update(SubscriptionPlan item, CoreContext context) {
        if (item.getId() == null) {
            throw new CoreException("INVALID_ARGUMENT", "SubscriptionPlan id required");
        }
        SubscriptionPlan current = subscriptionPlanRepository.findById(item.getId())
            .orElseThrow(() -> new CoreException("NOT_FOUND", "Subscription plan not found"));
        assertSameApp(current.getAppId(), context);
        if (item.getAppId() == null) {
            item.setAppId(current.getAppId());
        }
        item = map(item, context.getUserId(), ActionEnum.UPDATE);
        return subscriptionPlanRepository.save(item);
    }

    @Override
    @Transactional
    public void delete(SubscriptionPlan item, CoreContext context) {
        item.setIsActive(false);
        item = map(item, context.getUserId(), ActionEnum.DELETE);
        subscriptionPlanRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        SubscriptionPlan plan = findById(id, context);
        if (plan != null) {
            delete(plan, context);
        }
    }

    @Override
    public SubscriptionPlan findById(UUID id, CoreContext context) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(id).orElse(null);
        if (plan == null) {
            return null;
        }
        assertSameApp(plan.getAppId(), context);
        return plan;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionPlan> listByApp(UUID appId, boolean activeOnly) {
        UUID resolvedAppId = appId;
        if (activeOnly) {
            return subscriptionPlanRepository.findByAppIdAndCatalogActiveTrueOrderBySortOrderAscNameAsc(resolvedAppId);
        }
        return subscriptionPlanRepository.findByAppIdOrderBySortOrderAscNameAsc(resolvedAppId);
    }

    private static void assertSameApp(UUID planAppId, CoreContext context) {
        if (context.getAppId() != null && planAppId != null && !context.getAppId().equals(planAppId)) {
            throw new CoreException("FORBIDDEN", "Plan does not belong to the current app");
        }
    }

    @Override
    @Transactional
    public void hardDeleteById(UUID id, CoreContext context) {
        SubscriptionPlan found = findById(id, context);
        if (found != null) {
            subscriptionPlanRepository.delete(found);
        }
    }

    @Override
    @Transactional
    public SubscriptionPlan setActive(UUID id, boolean active, CoreContext context) {
        SubscriptionPlan found = findById(id, context);
        if (found == null) {
            throw new CoreException("NOT_FOUND", "SubscriptionPlan not found");
        }
        found.setIsActive(active);
        found = map(found, context.getUserId(), ActionEnum.UPDATE);
        found.setIsActive(active);
        return subscriptionPlanRepository.save(found);
    }
}
