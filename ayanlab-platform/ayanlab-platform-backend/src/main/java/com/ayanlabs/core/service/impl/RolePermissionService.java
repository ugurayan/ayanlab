package com.ayanlabs.core.service.impl;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.enums.ActionEnum;
import com.ayanlabs.core.entity.RolePermission;
import com.ayanlabs.core.exception.CoreException;
import com.ayanlabs.core.repository.RolePermissionRepository;
import com.ayanlabs.core.service.IRolePermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.ayanlabs.core.data.mapper.BaseMapper.map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RolePermissionService implements IRolePermissionService {

    private final RolePermissionRepository rolePermissionRepository;

    @Override
    @Transactional
    public RolePermission create(RolePermission item, CoreContext context) {
        item = map(item, context.getUserId(), ActionEnum.CREATE);
        return rolePermissionRepository.save(item);
    }

    @Override
    @Transactional
    public RolePermission update(RolePermission item, CoreContext context) {
        if (item.getId() == null) throw new CoreException("INVALID_ARGUMENT", "RolePermission id required");
        RolePermission current = rolePermissionRepository.findById(item.getId()).orElse(null);
        if (current == null) throw new CoreException("NOT_FOUND", "RolePermission not found");
        item = map(item, context.getUserId(), ActionEnum.UPDATE);
        return rolePermissionRepository.save(item);
    }

    @Override
    @Transactional
    public void delete(RolePermission item, CoreContext context) {
        item.setIsActive(false);
        item = map(item, context.getUserId(), ActionEnum.DELETE);
        rolePermissionRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        RolePermission rp = findById(id, context);
        if (rp != null) delete(rp, context);
    }

    @Override
    public RolePermission findById(UUID id, CoreContext context) {
        RolePermission rp;
        try {
            rp = rolePermissionRepository.findById(id).orElse(null);
        } catch (Exception ex) {
            throw CoreException.wrap(ex);
        }
        return rp;
    }

    @Override
    public List<RolePermission> findByRoleId(UUID roleId, CoreContext context) {
        if (roleId == null) {
            throw new CoreException("INVALID_ARGUMENT", "roleId required");
        }
        return rolePermissionRepository.findByRoleIdAndIsActiveTrue(roleId);
    }

    @Override
    @Transactional
    public void hardDeleteById(UUID id, CoreContext context) {
        RolePermission found = findById(id, context);
        if (found != null) {
            rolePermissionRepository.delete(found);
        }
    }

    @Override
    @Transactional
    public RolePermission setActive(UUID id, boolean active, CoreContext context) {
        RolePermission found = findById(id, context);
        if (found == null) {
            throw new CoreException("NOT_FOUND", "RolePermission not found");
        }
        found.setIsActive(active);
        found = map(found, context.getUserId(), ActionEnum.UPDATE);
        found.setIsActive(active);
        return rolePermissionRepository.save(found);
    }
}
