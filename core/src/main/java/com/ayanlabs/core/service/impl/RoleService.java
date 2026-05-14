package com.ayanlabs.core.service.impl;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.enums.ActionEnum;
import com.ayanlabs.core.entity.Role;
import com.ayanlabs.core.exception.CoreException;
import com.ayanlabs.core.repository.RoleRepository;
import com.ayanlabs.core.service.IRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.ayanlabs.core.data.mapper.BaseMapper.map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService {

    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public Role create(Role item, CoreContext context) {
        item = map(item, context.getUserId(), ActionEnum.CREATE);
        return roleRepository.save(item);
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
}
