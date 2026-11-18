package com.ayanlab.core.service.impl;

import com.ayanlab.core.data.CoreContext;
import com.ayanlab.core.data.enums.ActionEnum;
import com.ayanlab.core.entity.Role;
import com.ayanlab.core.exception.CoreException;
import com.ayanlab.core.repository.RoleRepository;
import com.ayanlab.core.service.IRoleService;
import com.ayanlab.core.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.ayanlab.core.data.mapper.BaseMapper.map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService {

    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public Role create(Role item, CoreContext context) {
        if (item.getAppId() == null) {
            item.setAppId(context.getAppId());
        }
        if (item.getAppId() == null) {
            throw new CoreException("INVALID_ARGUMENT", "Role appId required");
        }
        boolean duplicate = roleRepository.findByAppIdOrderByCodeAsc(item.getAppId()).stream()
            .anyMatch(r -> r.getCode().equals(item.getCode()) && Objects.equals(r.getOrganizationId(), item.getOrganizationId()));
        if (duplicate) {
            throw new CoreException("CONFLICT", "Role already exists in this app: " + item.getCode());
        }
        return roleRepository.save(map(item, context.getUserId(), ActionEnum.CREATE));
    }

    @Override
    @Transactional
    public Role update(Role item, CoreContext context) {
        if (item.getId() == null) throw new CoreException("INVALID_ARGUMENT", "Role id required");
        Role current = roleRepository.findById(item.getId()).orElse(null);
        if (current == null) throw new CoreException("NOT_FOUND", "Role not found");
        item = map(item, context.getUserId(), ActionEnum.UPDATE);
        return roleRepository.save(item);
    }

    @Override
    @Transactional
    public void delete(Role item, CoreContext context) {
        item.setIsActive(false);
        item = map(item, context.getUserId(), ActionEnum.DELETE);
        roleRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        Role r = findById(id, context);
        if (r != null) delete(r, context);
    }

    @Override
    public Role findById(UUID id, CoreContext context) {
        Role r;
        try {
            r = roleRepository.findById(id).orElse(null);
        } catch (Exception ex) {
            throw CoreException.wrap(ex);
        }
        return r;
    }

    @Override
    public List<Role> findAll(UUID appId, boolean includeInactive, CoreContext context) {
        // Only filter by app when the caller passes appId; the header app context must not hide other apps' roles.
        if (appId != null) {
            return roleRepository.findByAppIdOrderByCodeAsc(appId).stream()
                .filter(r -> includeInactive || Boolean.TRUE.equals(r.getIsActive()))
                .toList();
        }
        if (includeInactive) {
            return roleRepository.findAllByOrderByCodeAsc();
        }
        return roleRepository.findByIsActiveTrueOrderByCodeAsc();
    }

    @Override
    @Transactional
    public void hardDeleteById(UUID id, CoreContext context) {
        Role found = findById(id, context);
        if (found != null) {
            roleRepository.delete(found);
        }
    }

    @Override
    @Transactional
    public Role setActive(UUID id, boolean active, CoreContext context) {
        Role found = findById(id, context);
        if (found == null) {
            throw new CoreException("NOT_FOUND", "Role not found");
        }
        found.setIsActive(active);
        found = map(found, context.getUserId(), ActionEnum.UPDATE);
        found.setIsActive(active);
        return roleRepository.save(found);
    }
}
