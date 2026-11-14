package com.ayanlabs.core.service.impl;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.enums.ActionEnum;
import com.ayanlabs.core.entity.UserProfile;
import com.ayanlabs.core.exception.CoreException;
import com.ayanlabs.core.repository.UserProfileRepository;
import com.ayanlabs.core.service.IProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.ayanlabs.core.data.mapper.BaseMapper.map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService implements IProfileService {

    private final UserProfileRepository userProfileRepository;

    @Override
    @Transactional
    public UserProfile create(UserProfile item, CoreContext context) {
        item.setAppId(context.getAppId());
        item = map(item, context.getUserId(), ActionEnum.CREATE);
        return userProfileRepository.save(item);
    }

    @Override
    @Transactional
    public UserProfile update(UserProfile item, CoreContext context) {
        if (item.getId() == null) throw new CoreException("INVALID_ARGUMENT", "Profile id required");
        UserProfile current = userProfileRepository.findById(item.getId()).orElse(null);
        if (current == null) throw new CoreException("NOT_FOUND", "Profile not found");
        if (current.getAppId() != null && !current.getAppId().equals(context.getAppId()))
            throw new CoreException("FORBIDDEN", "Cannot modify profile of a different app");
        item.setAppId(context.getAppId());
        item = map(item, context.getUserId(), ActionEnum.UPDATE);
        return userProfileRepository.save(item);
    }

    @Override
    @Transactional
    public void delete(UserProfile item, CoreContext context) {
        item.setIsActive(false);
        item = map(item, context.getUserId(), ActionEnum.DELETE);
        userProfileRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        UserProfile p = findById(id, context);
        if (p != null) delete(p, context);
    }

    @Override
    public UserProfile findById(UUID id, CoreContext context) {
        UserProfile p;
        try {
            p = userProfileRepository.findById(id).orElse(null);
        } catch (Exception ex) {
            throw CoreException.wrap(ex);
        }
        if (p == null) return null;
        if (p.getAppId() != null && !p.getAppId().equals(context.getAppId())) {
            throw new CoreException("FORBIDDEN", "Profile does not belong to the current app");
        }
        return p;
    }

    @Override
    public List<UserProfile> findByApp(UUID appId, CoreContext context) {
        UUID resolvedAppId = appId != null ? appId : context.getAppId();
        if (resolvedAppId == null) {
            throw new CoreException("INVALID_ARGUMENT", "appId required");
        }
        return userProfileRepository.findByAppIdAndIsActiveTrueOrderByCreatedAtDesc(resolvedAppId);
    }

    @Override
    public List<UserProfile> findByUser(UUID userId, CoreContext context) {
        if (userId == null) {
            throw new CoreException("INVALID_ARGUMENT", "userId required");
        }
        return userProfileRepository.findByUserIdAndIsActiveTrueOrderByCreatedAtDesc(userId);
    }

    @Override
    public List<UserProfile> findByOrganization(UUID organizationId, UUID appId, CoreContext context) {
        if (organizationId == null) {
            throw new CoreException("INVALID_ARGUMENT", "organizationId required");
        }
        UUID resolvedAppId = appId != null ? appId : context.getAppId();
        if (resolvedAppId == null) {
            throw new CoreException("INVALID_ARGUMENT", "appId required");
        }
        return userProfileRepository.findByOrganizationIdAndAppIdAndIsActiveTrueOrderByCreatedAtDesc(
                organizationId, resolvedAppId);
    }
}
