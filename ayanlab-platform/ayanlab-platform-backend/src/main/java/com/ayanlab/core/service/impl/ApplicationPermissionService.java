package com.ayanlab.core.service.impl;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.enums.ActionEnum;
import com.ayanlab.core.entity.ApplicationPermission;
import com.ayanlab.core.exception.CoreException;
import com.ayanlab.core.repository.ApplicationPermissionRepository;
import com.ayanlab.core.service.IApplicationPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.ayanlab.core.mapping.BaseMapper.map;

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
        return list(appId != null ? appId : context.getAppId(), false, context);
    }

    @Override
    public List<ApplicationPermission> list(UUID appId, boolean includeInactive, CoreContext context) {
        if (appId != null) {
            return includeInactive
                    ? applicationPermissionRepository.findByAppId(appId)
                    : applicationPermissionRepository.findByAppIdAndIsActiveTrue(appId);
        }
        return includeInactive
                ? applicationPermissionRepository.findAll()
                : applicationPermissionRepository.findByIsActiveTrue();
    }

    @Override
    @Transactional
    public void hardDeleteById(UUID id, CoreContext context) {
        ApplicationPermission found = findById(id, context);
        if (found != null) {
            applicationPermissionRepository.delete(found);
        }
    }

    @Override
    @Transactional
    public ApplicationPermission setActive(UUID id, boolean active, CoreContext context) {
        ApplicationPermission found = findById(id, context);
        if (found == null) {
            throw new CoreException("NOT_FOUND", "ApplicationPermission not found");
        }
        found.setIsActive(active);
        found = map(found, context.getUserId(), ActionEnum.UPDATE);
        found.setIsActive(active);
        return applicationPermissionRepository.save(found);
    }
}
