package com.ayanlabs.core.service.impl;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.enums.ActionEnum;
import com.ayanlabs.core.entity.UserType;
import com.ayanlabs.core.exception.CoreException;
import com.ayanlabs.core.repository.UserTypeRepository;
import com.ayanlabs.core.service.IUserTypeService;
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
public class UserTypeService implements IUserTypeService {

    private final UserTypeRepository userTypeRepository;

    @Override
    @Transactional
    public UserType create(UserType item, CoreContext context) {
        item = map(item, context.getUserId(), ActionEnum.CREATE);
        return userTypeRepository.save(item);
    }

    @Override
    @Transactional
    public UserType update(UserType item, CoreContext context) {
        if (item.getId() == null) throw new CoreException("INVALID_ARGUMENT", "UserType id required");
        UserType current = userTypeRepository.findById(item.getId()).orElse(null);
        if (current == null) throw new CoreException("NOT_FOUND", "UserType not found");
        item = map(item, context.getUserId(), ActionEnum.UPDATE);
        return userTypeRepository.save(item);
    }

    @Override
    @Transactional
    public void delete(UserType item, CoreContext context) {
        item.setIsActive(false);
        item = map(item, context.getUserId(), ActionEnum.DELETE);
        userTypeRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        UserType ut = findById(id, context);
        if (ut != null) delete(ut, context);
    }

    @Override
    public UserType findById(UUID id, CoreContext context) {
        UserType ut;
        try {
            ut = userTypeRepository.findById(id).orElse(null);
        } catch (Exception ex) {
            throw CoreException.wrap(ex);
        }
        return ut;
    }

    @Override
    public List<UserType> findAll(CoreContext context) {
        return userTypeRepository.findByIsActiveTrueOrderByCodeAsc();
    }
}
