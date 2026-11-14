package com.ayanlabs.core.service.impl;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.enums.ActionEnum;
import com.ayanlabs.core.entity.PolicyGroupPermission;
import com.ayanlabs.core.exception.CoreException;
import com.ayanlabs.core.repository.PolicyGroupPermissionRepository;
import com.ayanlabs.core.service.IPolicyGroupPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.ayanlabs.core.data.mapper.BaseMapper.map;

@Service
@RequiredArgsConstructor
public class PolicyGroupPermissionService implements IPolicyGroupPermissionService {

    private final PolicyGroupPermissionRepository policyGroupPermissionRepository;

    @Override
    @Transactional
    public PolicyGroupPermission create(PolicyGroupPermission item, CoreContext context) {
        if (policyGroupPermissionRepository.existsByPolicyGroupIdAndPermissionId(
            item.getPolicyGroupId(), item.getPermissionId())) {
            throw new CoreException("CONFLICT", "PolicyGroupPermission already exists");
        }
        item = map(item, context.getUserId(), ActionEnum.CREATE);
        return policyGroupPermissionRepository.save(item);
    }

    @Override
    @Transactional
    public PolicyGroupPermission update(PolicyGroupPermission item, CoreContext context) {
        if (item.getId() == null) {
            throw new CoreException("INVALID_ARGUMENT", "PolicyGroupPermission id required");
        }
        if (!policyGroupPermissionRepository.existsById(item.getId())) {
            throw new CoreException("NOT_FOUND", "PolicyGroupPermission not found");
        }
        item = map(item, context.getUserId(), ActionEnum.UPDATE);
        return policyGroupPermissionRepository.save(item);
    }

    @Override
    @Transactional
    public void delete(PolicyGroupPermission item, CoreContext context) {
        item.setIsActive(false);
        item = map(item, context.getUserId(), ActionEnum.DELETE);
        policyGroupPermissionRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        PolicyGroupPermission item = findById(id, context);
        if (item != null) {
            delete(item, context);
        }
    }

    @Override
    public PolicyGroupPermission findById(UUID id, CoreContext context) {
        return policyGroupPermissionRepository.findById(id).orElse(null);
    }

    @Override
    public List<PolicyGroupPermission> findByPolicyGroupId(UUID policyGroupId, CoreContext context) {
        if (policyGroupId == null) {
            throw new CoreException("INVALID_ARGUMENT", "policyGroupId required");
        }
        return policyGroupPermissionRepository.findByPolicyGroupIdAndIsActiveTrue(policyGroupId);
    }
}
