package com.ayanlabs.core.service.impl;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.enums.ActionEnum;
import com.ayanlabs.core.entity.ApplicationOrganization;
import com.ayanlabs.core.exception.CoreException;
import com.ayanlabs.core.repository.ApplicationOrganizationRepository;
import com.ayanlabs.core.service.IApplicationOrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.ayanlabs.core.data.mapper.BaseMapper.map;

@Service
@RequiredArgsConstructor
public class ApplicationOrganizationService implements IApplicationOrganizationService {

    private final ApplicationOrganizationRepository applicationOrganizationRepository;

    @Override
    @Transactional
    public ApplicationOrganization create(ApplicationOrganization item, CoreContext context) {
        if (item.getAppId() == null) {
            item.setAppId(context.getAppId());
        }
        if (item.getOrgId() == null) {
            throw new CoreException("INVALID_ARGUMENT", "orgId required");
        }
        if (applicationOrganizationRepository.existsByAppIdAndOrgId(item.getAppId(), item.getOrgId())) {
            throw new CoreException("CONFLICT", "ApplicationOrganization already exists");
        }
        item = map(item, context.getUserId(), ActionEnum.CREATE);
        return applicationOrganizationRepository.save(item);
    }

    @Override
    @Transactional
    public ApplicationOrganization update(ApplicationOrganization item, CoreContext context) {
        if (item.getId() == null) {
            throw new CoreException("INVALID_ARGUMENT", "ApplicationOrganization id required");
        }
        if (!applicationOrganizationRepository.existsById(item.getId())) {
            throw new CoreException("NOT_FOUND", "ApplicationOrganization not found");
        }
        item = map(item, context.getUserId(), ActionEnum.UPDATE);
        return applicationOrganizationRepository.save(item);
    }

    @Override
    @Transactional
    public void delete(ApplicationOrganization item, CoreContext context) {
        item.setIsActive(false);
        item = map(item, context.getUserId(), ActionEnum.DELETE);
        applicationOrganizationRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        ApplicationOrganization item = findById(id, context);
        if (item != null) {
            delete(item, context);
        }
    }

    @Override
    public ApplicationOrganization findById(UUID id, CoreContext context) {
        return applicationOrganizationRepository.findById(id).orElse(null);
    }

    @Override
    public List<ApplicationOrganization> list(UUID appId, boolean includeInactive, CoreContext context) {
        if (appId != null) {
            return includeInactive
                    ? applicationOrganizationRepository.findByAppId(appId)
                    : applicationOrganizationRepository.findByAppIdAndIsActiveTrue(appId);
        }
        return includeInactive
                ? applicationOrganizationRepository.findAll()
                : applicationOrganizationRepository.findByIsActiveTrue();
    }

    @Override
    @Transactional
    public void hardDeleteById(UUID id, CoreContext context) {
        ApplicationOrganization found = findById(id, context);
        if (found != null) {
            applicationOrganizationRepository.delete(found);
        }
    }

    @Override
    @Transactional
    public ApplicationOrganization setActive(UUID id, boolean active, CoreContext context) {
        ApplicationOrganization found = findById(id, context);
        if (found == null) {
            throw new CoreException("NOT_FOUND", "ApplicationOrganization not found");
        }
        found.setIsActive(active);
        found = map(found, context.getUserId(), ActionEnum.UPDATE);
        found.setIsActive(active);
        return applicationOrganizationRepository.save(found);
    }
}
