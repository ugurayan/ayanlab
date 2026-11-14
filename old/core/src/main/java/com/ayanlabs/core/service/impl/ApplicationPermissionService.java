package com.ayanlabs.core.service.impl;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.enums.ActionEnum;
import com.ayanlabs.core.entity.ApplicationPermission;
import com.ayanlabs.core.exception.CoreException;
import com.ayanlabs.core.repository.ApplicationPermissionRepository;
import com.ayanlabs.core.service.IApplicationPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.ayanlabs.core.data.mapper.BaseMapper.map;

@Service
@RequiredArgsConstructor
public class ApplicationPermissionService implements IApplicationPermissionService {

    private final ApplicationPermissionRepository applicationPermissionRepository;

    @Override
    @Transactional
    public ApplicationPermission create(ApplicationPermission item, CoreContext context) {
        if (item.getAppId() == null) {
            item.setAppId(context.getAppId());
        }
        if (applicationPermissionRepository.existsByAppIdAndPermissionId(item.getAppId(), item.getPermissionId())) {
            throw new CoreException("CONFLICT", "ApplicationPermission already exists");
        }
        item = map(item, context.getUserId(), ActionEnum.CREATE);
        return applicationPermissionRepository.save(item);
    }

    @Override
    @Transactional
    public ApplicationPermission update(ApplicationPermission item, CoreContext context) {
        if (item.getId() == null) {
            throw new CoreException("INVALID_ARGUMENT", "ApplicationPermission id required");
        }
        if (!applicationPermissionRepository.existsById(item.getId())) {
            throw new CoreException("NOT_FOUND", "ApplicationPermission not found");
        }
        item = map(item, context.getUserId(), ActionEnum.UPDATE);
        return applicationPermissionRepository.save(item);
    }

    @Override
    @Transactional
    public void delete(ApplicationPermission item, CoreContext context) {
        item.setIsActive(false);
        item = map(item, context.getUserId(), ActionEnum.DELETE);
        applicationPermissionRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        ApplicationPermission item = findById(id, context);
        if (item != null) {
            delete(item, context);
        }
    }

    @Override
    public ApplicationPermission findById(UUID id, CoreContext context) {
        return applicationPermissionRepository.findById(id).orElse(null);
    }

    @Override
    public List<ApplicationPermission> findByAppId(UUID appId, CoreContext context) {
        UUID resolvedAppId = appId != null ? appId : context.getAppId();
        if (resolvedAppId == null) {
            throw new CoreException("INVALID_ARGUMENT", "appId required");
        }
        return applicationPermissionRepository.findByAppIdAndIsActiveTrue(resolvedAppId);
    }
}
