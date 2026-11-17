package com.ayanlabs.core.service.impl;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.enums.ActionEnum;
import com.ayanlabs.core.entity.ApplicationModule;
import com.ayanlabs.core.exception.CoreException;
import com.ayanlabs.core.repository.ApplicationModuleRepository;
import com.ayanlabs.core.service.IApplicationModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.ayanlabs.core.data.mapper.BaseMapper.map;

@Service
@RequiredArgsConstructor
public class ApplicationModuleService implements IApplicationModuleService {

    private final ApplicationModuleRepository applicationModuleRepository;

    @Override
    @Transactional
    public ApplicationModule create(ApplicationModule item, CoreContext context) {
        if (item.getAppId() == null) {
            item.setAppId(context.getAppId());
        }
        if (item.getModuleId() == null) {
            throw new CoreException("INVALID_ARGUMENT", "moduleId required");
        }
        if (applicationModuleRepository.existsByAppIdAndModuleId(item.getAppId(), item.getModuleId())) {
            throw new CoreException("CONFLICT", "ApplicationModule already exists");
        }
        item = map(item, context.getUserId(), ActionEnum.CREATE);
        return applicationModuleRepository.save(item);
    }

    @Override
    @Transactional
    public ApplicationModule update(ApplicationModule item, CoreContext context) {
        if (item.getId() == null) {
            throw new CoreException("INVALID_ARGUMENT", "ApplicationModule id required");
        }
        if (!applicationModuleRepository.existsById(item.getId())) {
            throw new CoreException("NOT_FOUND", "ApplicationModule not found");
        }
        item = map(item, context.getUserId(), ActionEnum.UPDATE);
        return applicationModuleRepository.save(item);
    }

    @Override
    @Transactional
    public void delete(ApplicationModule item, CoreContext context) {
        item.setIsActive(false);
        item = map(item, context.getUserId(), ActionEnum.DELETE);
        applicationModuleRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        ApplicationModule item = findById(id, context);
        if (item != null) {
            delete(item, context);
        }
    }

    @Override
    public ApplicationModule findById(UUID id, CoreContext context) {
        return applicationModuleRepository.findById(id).orElse(null);
    }

    @Override
    public List<ApplicationModule> list(UUID appId, boolean includeInactive, CoreContext context) {
        if (appId != null) {
            return includeInactive
                    ? applicationModuleRepository.findByAppId(appId)
                    : applicationModuleRepository.findByAppIdAndIsActiveTrue(appId);
        }
        return includeInactive
                ? applicationModuleRepository.findAll()
                : applicationModuleRepository.findByIsActiveTrue();
    }

    @Override
    @Transactional
    public void hardDeleteById(UUID id, CoreContext context) {
        ApplicationModule found = findById(id, context);
        if (found != null) {
            applicationModuleRepository.delete(found);
        }
    }

    @Override
    @Transactional
    public ApplicationModule setActive(UUID id, boolean active, CoreContext context) {
        ApplicationModule found = findById(id, context);
        if (found == null) {
            throw new CoreException("NOT_FOUND", "ApplicationModule not found");
        }
        found.setIsActive(active);
        found = map(found, context.getUserId(), ActionEnum.UPDATE);
        found.setIsActive(active);
        return applicationModuleRepository.save(found);
    }
}
