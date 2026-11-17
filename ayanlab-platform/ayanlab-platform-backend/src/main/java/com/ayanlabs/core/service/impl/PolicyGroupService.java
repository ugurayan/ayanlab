package com.ayanlabs.core.service.impl;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.enums.ActionEnum;
import com.ayanlabs.core.entity.PolicyGroup;
import com.ayanlabs.core.exception.CoreException;
import com.ayanlabs.core.repository.PolicyGroupRepository;
import com.ayanlabs.core.service.IPolicyGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.ayanlabs.core.data.mapper.BaseMapper.map;

@Service
@RequiredArgsConstructor
public class PolicyGroupService implements IPolicyGroupService {

    private final PolicyGroupRepository policyGroupRepository;

    @Override
    @Transactional
    public PolicyGroup create(PolicyGroup item, CoreContext context) {
        if (item.getAppId() == null) {
            item.setAppId(context.getAppId());
        }
        assertUniqueCode(item.getAppId(), item.getCode(), null);
        item = map(item, context.getUserId(), ActionEnum.CREATE);
        return policyGroupRepository.save(item);
    }

    @Override
    @Transactional
    public PolicyGroup update(PolicyGroup item, CoreContext context) {
        if (item.getId() == null) {
            throw new CoreException("INVALID_ARGUMENT", "PolicyGroup id required");
        }
        PolicyGroup current = policyGroupRepository.findById(item.getId())
            .orElseThrow(() -> new CoreException("NOT_FOUND", "PolicyGroup not found"));
        if (item.getAppId() == null) {
            item.setAppId(current.getAppId());
        }
        if (item.getCode() != null && !item.getCode().equals(current.getCode())) {
            assertUniqueCode(item.getAppId(), item.getCode(), item.getId());
        }
        item = map(item, context.getUserId(), ActionEnum.UPDATE);
        return policyGroupRepository.save(item);
    }

    @Override
    @Transactional
    public void delete(PolicyGroup item, CoreContext context) {
        item.setIsActive(false);
        item = map(item, context.getUserId(), ActionEnum.DELETE);
        policyGroupRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        PolicyGroup item = findById(id, context);
        if (item != null) {
            delete(item, context);
        }
    }

    @Override
    public PolicyGroup findById(UUID id, CoreContext context) {
        return policyGroupRepository.findById(id).orElse(null);
    }

    @Override
    public List<PolicyGroup> findByApp(UUID appId, CoreContext context) {
        UUID resolvedAppId = appId != null ? appId : context.getAppId();
        if (resolvedAppId == null) {
            throw new CoreException("INVALID_ARGUMENT", "appId required");
        }
        return policyGroupRepository.findByAppIdAndIsActiveTrueOrderByCodeAsc(resolvedAppId);
    }

    private void assertUniqueCode(UUID appId, String code, UUID excludeId) {
        policyGroupRepository.findByAppIdAndCode(appId, code).ifPresent(existing -> {
            if (excludeId == null || !excludeId.equals(existing.getId())) {
                throw new CoreException("CONFLICT", "PolicyGroup code already exists for app");
            }
        });
    }

    @Override
    @Transactional
    public void hardDeleteById(UUID id, CoreContext context) {
        PolicyGroup found = findById(id, context);
        if (found != null) {
            policyGroupRepository.delete(found);
        }
    }

    @Override
    @Transactional
    public PolicyGroup setActive(UUID id, boolean active, CoreContext context) {
        PolicyGroup found = findById(id, context);
        if (found == null) {
            throw new CoreException("NOT_FOUND", "PolicyGroup not found");
        }
        found.setIsActive(active);
        found = map(found, context.getUserId(), ActionEnum.UPDATE);
        found.setIsActive(active);
        return policyGroupRepository.save(found);
    }
}
