package com.ayanlab.core.service.impl;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.enums.ActionEnum;
import com.ayanlab.core.entity.ProfilePolicyGroup;
import com.ayanlab.core.exception.CoreException;
import com.ayanlab.core.repository.ProfilePolicyGroupRepository;
import com.ayanlab.core.service.IProfilePolicyGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.ayanlab.core.mapping.BaseMapper.map;

@Service
@RequiredArgsConstructor
public class ProfilePolicyGroupService implements IProfilePolicyGroupService {

    private final ProfilePolicyGroupRepository profilePolicyGroupRepository;

    @Override
    @Transactional
    public ProfilePolicyGroup create(ProfilePolicyGroup item, CoreContext context) {
        if (profilePolicyGroupRepository.existsByProfileIdAndPolicyGroupId(
            item.getProfileId(), item.getPolicyGroupId())) {
            throw new CoreException("CONFLICT", "ProfilePolicyGroup already exists");
        }
        if (item.getGrantedAt() == null) {
            item.setGrantedAt(Instant.now());
        }
        if (item.getGrantedBy() == null) {
            item.setGrantedBy(context.getUserId());
        }
        item = map(item, context.getUserId(), ActionEnum.CREATE);
        return profilePolicyGroupRepository.save(item);
    }

    @Override
    @Transactional
    public ProfilePolicyGroup update(ProfilePolicyGroup item, CoreContext context) {
        if (item.getId() == null) {
            throw new CoreException("INVALID_ARGUMENT", "ProfilePolicyGroup id required");
        }
        if (!profilePolicyGroupRepository.existsById(item.getId())) {
            throw new CoreException("NOT_FOUND", "ProfilePolicyGroup not found");
        }
        item = map(item, context.getUserId(), ActionEnum.UPDATE);
        return profilePolicyGroupRepository.save(item);
    }

    @Override
    @Transactional
    public void delete(ProfilePolicyGroup item, CoreContext context) {
        item.setIsActive(false);
        item = map(item, context.getUserId(), ActionEnum.DELETE);
        profilePolicyGroupRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        ProfilePolicyGroup item = findById(id, context);
        if (item != null) {
            delete(item, context);
        }
    }

    @Override
    public ProfilePolicyGroup findById(UUID id, CoreContext context) {
        return profilePolicyGroupRepository.findById(id).orElse(null);
    }

    @Override
    public List<ProfilePolicyGroup> findByProfileId(UUID profileId, CoreContext context) {
        if (profileId == null) {
            throw new CoreException("INVALID_ARGUMENT", "profileId required");
        }
        return profilePolicyGroupRepository.findByProfileIdAndIsActiveTrue(profileId);
    }

    @Override
    @Transactional
    public void hardDeleteById(UUID id, CoreContext context) {
        ProfilePolicyGroup found = findById(id, context);
        if (found != null) {
            profilePolicyGroupRepository.delete(found);
        }
    }

    @Override
    @Transactional
    public ProfilePolicyGroup setActive(UUID id, boolean active, CoreContext context) {
        ProfilePolicyGroup found = findById(id, context);
        if (found == null) {
            throw new CoreException("NOT_FOUND", "ProfilePolicyGroup not found");
        }
        found.setIsActive(active);
        found = map(found, context.getUserId(), ActionEnum.UPDATE);
        found.setIsActive(active);
        return profilePolicyGroupRepository.save(found);
    }
}
