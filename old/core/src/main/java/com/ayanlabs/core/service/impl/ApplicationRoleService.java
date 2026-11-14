package com.ayanlabs.core.service.impl;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.enums.ActionEnum;
import com.ayanlabs.core.entity.ApplicationRole;
import com.ayanlabs.core.exception.CoreException;
import com.ayanlabs.core.repository.ApplicationRoleRepository;
import com.ayanlabs.core.service.IApplicationRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.ayanlabs.core.data.mapper.BaseMapper.map;

@Service
@RequiredArgsConstructor
public class ApplicationRoleService implements IApplicationRoleService {

    private final ApplicationRoleRepository applicationRoleRepository;

    @Override
    @Transactional
    public ApplicationRole create(ApplicationRole item, CoreContext context) {
        if (item.getAppId() == null) {
            item.setAppId(context.getAppId());
        }
        if (applicationRoleRepository.existsByAppIdAndRoleId(item.getAppId(), item.getRoleId())) {
            throw new CoreException("CONFLICT", "ApplicationRole already exists");
        }
        item = map(item, context.getUserId(), ActionEnum.CREATE);
        return applicationRoleRepository.save(item);
    }

    @Override
    @Transactional
    public ApplicationRole update(ApplicationRole item, CoreContext context) {
        if (item.getId() == null) {
            throw new CoreException("INVALID_ARGUMENT", "ApplicationRole id required");
        }
        if (!applicationRoleRepository.existsById(item.getId())) {
            throw new CoreException("NOT_FOUND", "ApplicationRole not found");
        }
        item = map(item, context.getUserId(), ActionEnum.UPDATE);
        return applicationRoleRepository.save(item);
    }

    @Override
    @Transactional
    public void delete(ApplicationRole item, CoreContext context) {
        item.setIsActive(false);
        item = map(item, context.getUserId(), ActionEnum.DELETE);
        applicationRoleRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        ApplicationRole item = findById(id, context);
        if (item != null) {
            delete(item, context);
        }
    }

    @Override
    public ApplicationRole findById(UUID id, CoreContext context) {
        return applicationRoleRepository.findById(id).orElse(null);
    }

    @Override
    public List<ApplicationRole> findByAppId(UUID appId, CoreContext context) {
        UUID resolvedAppId = appId != null ? appId : context.getAppId();
        if (resolvedAppId == null) {
            throw new CoreException("INVALID_ARGUMENT", "appId required");
        }
        return applicationRoleRepository.findByAppIdAndIsActiveTrue(resolvedAppId);
    }
}
