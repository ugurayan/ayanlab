package com.ayanlab.core.service.impl;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.enums.ActionEnum;
import com.ayanlab.core.entity.ApplicationUser;
import com.ayanlab.core.exception.CoreException;
import com.ayanlab.core.repository.ApplicationUserRepository;
import com.ayanlab.core.service.IApplicationUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.ayanlab.core.mapping.BaseMapper.map;

@Service
@RequiredArgsConstructor
public class ApplicationUserService implements IApplicationUserService {

    private final ApplicationUserRepository applicationUserRepository;

    @Override
    @Transactional
    public ApplicationUser create(ApplicationUser item, CoreContext context) {
        if (item.getAppId() == null) {
            item.setAppId(context.getAppId());
        }
        if (item.getUserId() == null) {
            throw new CoreException("INVALID_ARGUMENT", "userId required");
        }
        if (applicationUserRepository.existsByAppIdAndUserId(item.getAppId(), item.getUserId())) {
            throw new CoreException("CONFLICT", "ApplicationUser already exists");
        }
        item = map(item, context.getUserId(), ActionEnum.CREATE);
        return applicationUserRepository.save(item);
    }

    @Override
    @Transactional
    public ApplicationUser update(ApplicationUser item, CoreContext context) {
        if (item.getId() == null) {
            throw new CoreException("INVALID_ARGUMENT", "ApplicationUser id required");
        }
        if (!applicationUserRepository.existsById(item.getId())) {
            throw new CoreException("NOT_FOUND", "ApplicationUser not found");
        }
        item = map(item, context.getUserId(), ActionEnum.UPDATE);
        return applicationUserRepository.save(item);
    }

    @Override
    @Transactional
    public void delete(ApplicationUser item, CoreContext context) {
        item.setIsActive(false);
        item = map(item, context.getUserId(), ActionEnum.DELETE);
        applicationUserRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        ApplicationUser item = findById(id, context);
        if (item != null) {
            delete(item, context);
        }
    }

    @Override
    public ApplicationUser findById(UUID id, CoreContext context) {
        return applicationUserRepository.findById(id).orElse(null);
    }

    @Override
    public List<ApplicationUser> list(UUID appId, boolean includeInactive, CoreContext context) {
        UUID resolvedAppId = appId != null ? appId : null;
        if (resolvedAppId != null) {
            return includeInactive
                    ? applicationUserRepository.findByAppId(resolvedAppId)
                    : applicationUserRepository.findByAppIdAndIsActiveTrue(resolvedAppId);
        }
        return includeInactive ? applicationUserRepository.findAll() : applicationUserRepository.findByIsActiveTrue();
    }

    @Override
    @Transactional
    public void hardDeleteById(UUID id, CoreContext context) {
        ApplicationUser found = findById(id, context);
        if (found != null) {
            applicationUserRepository.delete(found);
        }
    }

    @Override
    @Transactional
    public ApplicationUser setActive(UUID id, boolean active, CoreContext context) {
        ApplicationUser found = findById(id, context);
        if (found == null) {
            throw new CoreException("NOT_FOUND", "ApplicationUser not found");
        }
        found.setIsActive(active);
        found = map(found, context.getUserId(), ActionEnum.UPDATE);
        found.setIsActive(active);
        return applicationUserRepository.save(found);
    }
}
