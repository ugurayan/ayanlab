package com.ayanlabs.core.service.impl;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.enums.ActionEnum;
import com.ayanlabs.core.entity.RolePolicyGroup;
import com.ayanlabs.core.exception.CoreException;
import com.ayanlabs.core.repository.RolePolicyGroupRepository;
import com.ayanlabs.core.service.IRolePolicyGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.ayanlabs.core.data.mapper.BaseMapper.map;

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
}
