package com.ayanlab.core.service.impl;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.enums.ActionEnum;
import com.ayanlab.core.entity.SubscriptionPlan;
import com.ayanlab.core.entity.SubscriptionPlanPrice;
import com.ayanlab.core.exception.CoreException;
import com.ayanlab.core.repository.SubscriptionPlanPriceRepository;
import com.ayanlab.core.repository.SubscriptionPlanRepository;
import com.ayanlab.core.service.ISubscriptionPlanPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.ayanlab.core.mapping.BaseMapper.map;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanPriceService implements ISubscriptionPlanPriceService {

    private final SubscriptionPlanPriceRepository priceRepository;
    private final SubscriptionPlanRepository planRepository;

    @Override
    @Transactional
    public SubscriptionPlanPrice create(SubscriptionPlanPrice item, CoreContext context) {
        assertPlanAccessible(item.getSubscriptionPlanId(), context);
        item = map(item, context.getUserId(), ActionEnum.CREATE);
        return priceRepository.save(item);
    }

    @Override
    @Transactional
    public SubscriptionPlanPrice update(SubscriptionPlanPrice item, CoreContext context) {
        if (item.getId() == null) {
            throw new CoreException("INVALID_ARGUMENT", "SubscriptionPlanPrice id required");
        }
        SubscriptionPlanPrice current = priceRepository.findById(item.getId())
            .orElseThrow(() -> new CoreException("NOT_FOUND", "Plan price not found"));
        assertPlanAccessible(current.getSubscriptionPlanId(), context);
        if (item.getSubscriptionPlanId() == null) {
            item.setSubscriptionPlanId(current.getSubscriptionPlanId());
        }
        item = map(item, context.getUserId(), ActionEnum.UPDATE);
        return priceRepository.save(item);
    }

    @Override
    @Transactional
    public void delete(SubscriptionPlanPrice item, CoreContext context) {
        item.setIsActive(false);
        item = map(item, context.getUserId(), ActionEnum.DELETE);
        priceRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        SubscriptionPlanPrice price = findById(id, context);
        if (price != null) {
            delete(price, context);
        }
    }

    @Override
    public SubscriptionPlanPrice findById(UUID id, CoreContext context) {
        SubscriptionPlanPrice price = priceRepository.findById(id).orElse(null);
        if (price == null) {
            return null;
        }
        assertPlanAccessible(price.getSubscriptionPlanId(), context);
        return price;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionPlanPrice> listByPlan(UUID subscriptionPlanId, boolean activeOnly) {
        if (activeOnly) {
            return priceRepository.findBySubscriptionPlanIdAndCatalogActiveTrueOrderByBillingPeriodAsc(subscriptionPlanId);
        }
        return priceRepository.findBySubscriptionPlanIdOrderByBillingPeriodAsc(subscriptionPlanId);
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
        SubscriptionPlanPrice found = findById(id, context);
        if (found != null) {
            priceRepository.delete(found);
        }
    }

    @Override
    @Transactional
    public SubscriptionPlanPrice setActive(UUID id, boolean active, CoreContext context) {
        SubscriptionPlanPrice found = findById(id, context);
        if (found == null) {
            throw new CoreException("NOT_FOUND", "SubscriptionPlanPrice not found");
        }
        found.setIsActive(active);
        found = map(found, context.getUserId(), ActionEnum.UPDATE);
        found.setIsActive(active);
        return priceRepository.save(found);
    }
}
