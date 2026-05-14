package com.ayanlabs.core.service.impl;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.enums.ActionEnum;
import com.ayanlabs.core.entity.Permission;
import com.ayanlabs.core.exception.CoreException;
import com.ayanlabs.core.repository.PermissionRepository;
import com.ayanlabs.core.service.IPermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.ayanlabs.core.data.mapper.BaseMapper.map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionService implements IPermissionService {

    private final PermissionRepository permissionRepository;

    @Override
    @Transactional
    public Permission create(Permission item, CoreContext context) {
        item = map(item, context.getUserId(), ActionEnum.CREATE);
        return permissionRepository.save(item);
    }

    @Override
    @Transactional
    public Permission update(Permission item, CoreContext context) {
        if (item.getId() == null) throw new CoreException("INVALID_ARGUMENT", "Permission id required");
        Permission current = permissionRepository.findById(item.getId()).orElse(null);
        if (current == null) throw new CoreException("NOT_FOUND", "Permission not found");
        item = map(item, context.getUserId(), ActionEnum.UPDATE);
        return permissionRepository.save(item);
    }

    @Override
    @Transactional
    public void delete(Permission item, CoreContext context) {
        item.setIsActive(false);
        item = map(item, context.getUserId(), ActionEnum.DELETE);
        permissionRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        Permission p = findById(id, context);
        if (p != null) delete(p, context);
    }

    @Override
    public Permission findById(UUID id, CoreContext context) {
        Permission p;
        try {
            p = permissionRepository.findById(id).orElse(null);
        } catch (Exception ex) {
            throw CoreException.wrap(ex);
        }
        return p;
    }
}
