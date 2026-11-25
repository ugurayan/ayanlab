package com.ayanlab.core.service.impl;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.enums.ActionEnum;
import com.ayanlab.core.enums.PermissionEffectEnum;
import com.ayanlab.core.entity.ProfilePermission;
import com.ayanlab.core.exception.CoreException;
import com.ayanlab.core.repository.ProfilePermissionRepository;
import com.ayanlab.core.service.IProfilePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.ayanlab.core.mapping.BaseMapper.map;

@Service
@RequiredArgsConstructor
public class ProfilePermissionService implements IProfilePermissionService {

    private final ProfilePermissionRepository profilePermissionRepository;

    @Override
    @Transactional
    public ProfilePermission create(ProfilePermission item, CoreContext context) {
        if (profilePermissionRepository.existsByProfileIdAndPermissionId(
            item.getProfileId(), item.getPermissionId())) {
            throw new CoreException("CONFLICT", "ProfilePermission already exists");
        }
        if (item.getEffect() == null) {
            item.setEffect(PermissionEffectEnum.GRANT);
        }
        item = map(item, context.getUserId(), ActionEnum.CREATE);
        return profilePermissionRepository.save(item);
    }

    @Override
    @Transactional
    public ProfilePermission update(ProfilePermission item, CoreContext context) {
        if (item.getId() == null) {
            throw new CoreException("INVALID_ARGUMENT", "ProfilePermission id required");
        }
        if (!profilePermissionRepository.existsById(item.getId())) {
            throw new CoreException("NOT_FOUND", "ProfilePermission not found");
        }
        if (item.getEffect() == null) {
            item.setEffect(PermissionEffectEnum.GRANT);
        }
        item = map(item, context.getUserId(), ActionEnum.UPDATE);
        return profilePermissionRepository.save(item);
    }

    @Override
    @Transactional
    public void delete(ProfilePermission item, CoreContext context) {
        item.setIsActive(false);
        item = map(item, context.getUserId(), ActionEnum.DELETE);
        profilePermissionRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        ProfilePermission item = findById(id, context);
        if (item != null) {
            delete(item, context);
        }
    }

    @Override
    public ProfilePermission findById(UUID id, CoreContext context) {
        return profilePermissionRepository.findById(id).orElse(null);
    }

    @Override
    public List<ProfilePermission> findByProfileId(UUID profileId, CoreContext context) {
        if (profileId == null) {
            throw new CoreException("INVALID_ARGUMENT", "profileId required");
        }
        return profilePermissionRepository.findByProfileIdAndIsActiveTrue(profileId);
    }

    @Override
    @Transactional
    public void hardDeleteById(UUID id, CoreContext context) {
        ProfilePermission found = findById(id, context);
        if (found != null) {
            profilePermissionRepository.delete(found);
        }
    }

    @Override
    @Transactional
    public ProfilePermission setActive(UUID id, boolean active, CoreContext context) {
        ProfilePermission found = findById(id, context);
        if (found == null) {
            throw new CoreException("NOT_FOUND", "ProfilePermission not found");
        }
        found.setIsActive(active);
        found = map(found, context.getUserId(), ActionEnum.UPDATE);
        found.setIsActive(active);
        return profilePermissionRepository.save(found);
    }
}
