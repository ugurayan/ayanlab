package com.ayanlab.core.service.impl;

import com.ayanlab.core.data.CoreContext;
import com.ayanlab.core.data.enums.ActionEnum;
import com.ayanlab.core.entity.BankAccount;
import com.ayanlab.core.exception.CoreException;
import com.ayanlab.core.repository.BankAccountRepository;
import com.ayanlab.core.service.IBankAccountService;
import com.ayanlab.core.service.support.ProfileScopeSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.ayanlab.core.data.mapper.BaseMapper.map;

@Service
@RequiredArgsConstructor
public class BankAccountService implements IBankAccountService {

    private final BankAccountRepository bankAccountRepository;

    @Override
    @Transactional
    public BankAccount create(BankAccount item, CoreContext context) {
        if (item.getAppId() == null) {
            item.setAppId(context.getAppId());
        }
        item.setProfileId(ProfileScopeSupport.resolveProfileId(item.getProfileId(), context));
        ProfileScopeSupport.assertAppScope(item.getAppId(), context);
        item = map(item, context.getUserId(), ActionEnum.CREATE);
        return bankAccountRepository.save(item);
    }

    @Override
    @Transactional
    public BankAccount update(BankAccount item, CoreContext context) {
        if (item.getId() == null) {
            throw new CoreException("INVALID_ARGUMENT", "BankAccount id required");
        }
        BankAccount current = bankAccountRepository.findById(item.getId())
            .orElseThrow(() -> new CoreException("NOT_FOUND", "Bank account not found"));
        ProfileScopeSupport.assertProfileScope(current.getProfileId(), context);
        ProfileScopeSupport.assertAppScope(current.getAppId(), context);
        if (item.getAppId() == null) {
            item.setAppId(current.getAppId());
        }
        if (item.getProfileId() == null) {
            item.setProfileId(current.getProfileId());
        }
        item = map(item, context.getUserId(), ActionEnum.UPDATE);
        return bankAccountRepository.save(item);
    }

    @Override
    @Transactional
    public void delete(BankAccount item, CoreContext context) {
        item.setIsActive(false);
        bankAccountRepository.save(map(item, context.getUserId(), ActionEnum.DELETE));
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        BankAccount row = findById(id, context);
        if (row != null) {
            delete(row, context);
        }
    }

    @Override
    public BankAccount findById(UUID id, CoreContext context) {
        BankAccount row = bankAccountRepository.findById(id).orElse(null);
        if (row != null) {
            ProfileScopeSupport.assertProfileScope(row.getProfileId(), context);
            ProfileScopeSupport.assertAppScope(row.getAppId(), context);
        }
        return row;
    }

    @Override
    public List<BankAccount> listByProfile(UUID profileId, UUID appId, CoreContext context) {
        UUID pid = profileId != null ? profileId : ProfileScopeSupport.requireProfileId(context);
        UUID aid = appId != null ? appId : context.getAppId();
        if (aid != null) {
            return bankAccountRepository.findByProfileIdAndAppIdAndIsActiveTrueOrderByCreatedAtDesc(pid, aid);
        }
        return bankAccountRepository.findByProfileIdAndIsActiveTrueOrderByCreatedAtDesc(pid);
    }

    @Override
    @Transactional
    public void hardDeleteById(UUID id, CoreContext context) {
        BankAccount found = findById(id, context);
        if (found != null) {
            bankAccountRepository.delete(found);
        }
    }

    @Override
    @Transactional
    public BankAccount setActive(UUID id, boolean active, CoreContext context) {
        BankAccount found = findById(id, context);
        if (found == null) {
            throw new CoreException("NOT_FOUND", "BankAccount not found");
        }
        found.setIsActive(active);
        found = map(found, context.getUserId(), ActionEnum.UPDATE);
        found.setIsActive(active);
        return bankAccountRepository.save(found);
    }
}