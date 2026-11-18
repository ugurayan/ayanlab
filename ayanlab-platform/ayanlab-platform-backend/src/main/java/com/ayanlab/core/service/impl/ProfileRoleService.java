package com.ayanlab.core.service.impl;

import com.ayanlab.core.data.CoreContext;
import com.ayanlab.core.data.enums.ActionEnum;
import com.ayanlab.core.entity.ProfileRole;
import com.ayanlab.core.exception.CoreException;
import com.ayanlab.core.repository.ProfileRoleRepository;
import com.ayanlab.core.service.IProfileRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.ayanlab.core.data.mapper.BaseMapper.map;

@Service
@RequiredArgsConstructor
public class ProfileRoleService implements IProfileRoleService {

    private final ProfileRoleRepository profileRoleRepository;

    @Override
    @Transactional
    public ProfileRole create(ProfileRole item, CoreContext context) {
        if (profileRoleRepository.existsByProfileIdAndRoleId(item.getProfileId(), item.getRoleId())) {
            throw new CoreException("CONFLICT", "ProfileRole already exists");
        }
        if (item.getGrantedAt() == null) {
            item.setGrantedAt(Instant.now());
        }
        if (item.getGrantedBy() == null) {
            item.setGrantedBy(context.getUserId());
        }
        item = map(item, context.getUserId(), ActionEnum.CREATE);
        return profileRoleRepository.save(item);
    }

    @Override
    @Transactional
    public ProfileRole update(ProfileRole item, CoreContext context) {
        if (item.getId() == null) {
            throw new CoreException("INVALID_ARGUMENT", "ProfileRole id required");
        }
        if (!profileRoleRepository.existsById(item.getId())) {
            throw new CoreException("NOT_FOUND", "ProfileRole not found");
        }
        item = map(item, context.getUserId(), ActionEnum.UPDATE);
        return profileRoleRepository.save(item);
    }

    @Override
    @Transactional
    public void delete(ProfileRole item, CoreContext context) {
        item.setIsActive(false);
        item = map(item, context.getUserId(), ActionEnum.DELETE);
        profileRoleRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        ProfileRole item = findById(id, context);
        if (item != null) {
            delete(item, context);
        }
    }

    @Override
    public ProfileRole findById(UUID id, CoreContext context) {
        return profileRoleRepository.findById(id).orElse(null);
    }

    @Override
    public List<ProfileRole> findByProfileId(UUID profileId, CoreContext context) {
        if (profileId == null) {
            throw new CoreException("INVALID_ARGUMENT", "profileId required");
        }
        return profileRoleRepository.findByProfileIdAndIsActiveTrue(profileId);
    }

    @Override
    @Transactional
    public void hardDeleteById(UUID id, CoreContext context) {
        ProfileRole found = findById(id, context);
        if (found != null) {
            profileRoleRepository.delete(found);
        }
    }

    @Override
    @Transactional
    public ProfileRole setActive(UUID id, boolean active, CoreContext context) {
        ProfileRole found = findById(id, context);
        if (found == null) {
            throw new CoreException("NOT_FOUND", "ProfileRole not found");
        }
        found.setIsActive(active);
        found = map(found, context.getUserId(), ActionEnum.UPDATE);
        found.setIsActive(active);
        return profileRoleRepository.save(found);
    }
}
