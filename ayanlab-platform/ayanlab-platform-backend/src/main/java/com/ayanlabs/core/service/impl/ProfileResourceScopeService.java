package com.ayanlabs.core.service.impl;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.enums.ActionEnum;
import com.ayanlabs.core.entity.ProfileResourceScope;
import com.ayanlabs.core.exception.CoreException;
import com.ayanlabs.core.repository.ProfileResourceScopeRepository;
import com.ayanlabs.core.service.IProfileResourceScopeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.ayanlabs.core.data.mapper.BaseMapper.map;

@Service
@RequiredArgsConstructor
public class ProfileResourceScopeService implements IProfileResourceScopeService {

    private final ProfileResourceScopeRepository profileResourceScopeRepository;

    @Override
    @Transactional
    public ProfileResourceScope create(ProfileResourceScope item, CoreContext context) {
        item = map(item, context.getUserId(), ActionEnum.CREATE);
        return profileResourceScopeRepository.save(item);
    }

    @Override
    @Transactional
    public ProfileResourceScope update(ProfileResourceScope item, CoreContext context) {
        if (item.getId() == null) {
            throw new CoreException("INVALID_ARGUMENT", "ProfileResourceScope id required");
        }
        if (!profileResourceScopeRepository.existsById(item.getId())) {
            throw new CoreException("NOT_FOUND", "ProfileResourceScope not found");
        }
        item = map(item, context.getUserId(), ActionEnum.UPDATE);
        return profileResourceScopeRepository.save(item);
    }

    @Override
    @Transactional
    public void delete(ProfileResourceScope item, CoreContext context) {
        item.setIsActive(false);
        item = map(item, context.getUserId(), ActionEnum.DELETE);
        profileResourceScopeRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        ProfileResourceScope item = findById(id, context);
        if (item != null) {
            delete(item, context);
        }
    }

    @Override
    public ProfileResourceScope findById(UUID id, CoreContext context) {
        return profileResourceScopeRepository.findById(id).orElse(null);
    }

    @Override
    public List<ProfileResourceScope> findByProfile(UUID profileId, CoreContext context) {
        return profileResourceScopeRepository.findByProfileIdAndIsActiveTrue(profileId);
    }

    @Override
    @Transactional
    public void hardDeleteById(UUID id, CoreContext context) {
        ProfileResourceScope found = findById(id, context);
        if (found != null) {
            profileResourceScopeRepository.delete(found);
        }
    }

    @Override
    @Transactional
    public ProfileResourceScope setActive(UUID id, boolean active, CoreContext context) {
        ProfileResourceScope found = findById(id, context);
        if (found == null) {
            throw new CoreException("NOT_FOUND", "ProfileResourceScope not found");
        }
        found.setIsActive(active);
        found = map(found, context.getUserId(), ActionEnum.UPDATE);
        found.setIsActive(active);
        return profileResourceScopeRepository.save(found);
    }
}
