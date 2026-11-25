package com.ayanlab.core.service.impl;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.enums.ActionEnum;
import com.ayanlab.core.entity.RolePolicyGroup;
import com.ayanlab.core.exception.CoreException;
import com.ayanlab.core.repository.RolePolicyGroupRepository;
import com.ayanlab.core.service.IRolePolicyGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.ayanlab.core.mapping.BaseMapper.map;

@Service
@RequiredArgsConstructor
public class RolePolicyGroupService implements IRolePolicyGroupService {

    private final RolePolicyGroupRepository rolePolicyGroupRepository;

    @Override
    @Transactional
    public RolePolicyGroup create(RolePolicyGroup item, CoreContext context) {
        if (rolePolicyGroupRepository.existsByRoleIdAndPolicyGroupId(item.getRoleId(), item.getPolicyGroupId())) {
            throw new CoreException("CONFLICT", "RolePolicyGroup already exists");
        }
        item = map(item, context.getUserId(), ActionEnum.CREATE);
        return rolePolicyGroupRepository.save(item);
    }

    @Override
    @Transactional
    public RolePolicyGroup update(RolePolicyGroup item, CoreContext context) {
        if (item.getId() == null) {
            throw new CoreException("INVALID_ARGUMENT", "RolePolicyGroup id required");
        }
        if (!rolePolicyGroupRepository.existsById(item.getId())) {
            throw new CoreException("NOT_FOUND", "RolePolicyGroup not found");
        }
        item = map(item, context.getUserId(), ActionEnum.UPDATE);
        return rolePolicyGroupRepository.save(item);
    }

    @Override
    @Transactional
    public void delete(RolePolicyGroup item, CoreContext context) {
        item.setIsActive(false);
        item = map(item, context.getUserId(), ActionEnum.DELETE);
        rolePolicyGroupRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        RolePolicyGroup item = findById(id, context);
        if (item != null) {
            delete(item, context);
        }
    }

    @Override
    public RolePolicyGroup findById(UUID id, CoreContext context) {
        return rolePolicyGroupRepository.findById(id).orElse(null);
    }

    @Override
    public List<RolePolicyGroup> findByRoleId(UUID roleId, CoreContext context) {
        if (roleId == null) {
            throw new CoreException("INVALID_ARGUMENT", "roleId required");
        }
        return rolePolicyGroupRepository.findByRoleIdAndIsActiveTrue(roleId);
    }

    @Override
    @Transactional
    public void hardDeleteById(UUID id, CoreContext context) {
        RolePolicyGroup found = findById(id, context);
        if (found != null) {
            rolePolicyGroupRepository.delete(found);
        }
    }

    @Override
    @Transactional
    public RolePolicyGroup setActive(UUID id, boolean active, CoreContext context) {
        RolePolicyGroup found = findById(id, context);
        if (found == null) {
            throw new CoreException("NOT_FOUND", "RolePolicyGroup not found");
        }
        found.setIsActive(active);
        found = map(found, context.getUserId(), ActionEnum.UPDATE);
        found.setIsActive(active);
        return rolePolicyGroupRepository.save(found);
    }
}
