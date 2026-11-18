package com.ayanlab.core.service.impl;

import com.ayanlab.core.data.CoreContext;
import com.ayanlab.core.data.enums.ActionEnum;
import com.ayanlab.core.entity.BankCard;
import com.ayanlab.core.exception.CoreException;
import com.ayanlab.core.repository.BankCardRepository;
import com.ayanlab.core.service.IBankCardService;
import com.ayanlab.core.service.support.ProfileScopeSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.ayanlab.core.data.mapper.BaseMapper.map;

@Service
@RequiredArgsConstructor
public class BankCardService implements IBankCardService {

    private final BankCardRepository bankCardRepository;

    @Override
    @Transactional
    public BankCard create(BankCard item, CoreContext context) {
        if (item.getAppId() == null) {
            item.setAppId(context.getAppId());
        }
        item.setProfileId(ProfileScopeSupport.resolveProfileId(item.getProfileId(), context));
        ProfileScopeSupport.assertAppScope(item.getAppId(), context);
        item = map(item, context.getUserId(), ActionEnum.CREATE);
        return bankCardRepository.save(item);
    }

    @Override
    @Transactional
    public BankCard update(BankCard item, CoreContext context) {
        if (item.getId() == null) {
            throw new CoreException("INVALID_ARGUMENT", "BankCard id required");
        }
        BankCard current = bankCardRepository.findById(item.getId())
            .orElseThrow(() -> new CoreException("NOT_FOUND", "Bank card not found"));
        ProfileScopeSupport.assertProfileScope(current.getProfileId(), context);
        ProfileScopeSupport.assertAppScope(current.getAppId(), context);
        if (item.getAppId() == null) {
            item.setAppId(current.getAppId());
        }
        if (item.getProfileId() == null) {
            item.setProfileId(current.getProfileId());
        }
        item = map(item, context.getUserId(), ActionEnum.UPDATE);
        return bankCardRepository.save(item);
    }

    @Override
    @Transactional
    public void delete(BankCard item, CoreContext context) {
        item.setIsActive(false);
        bankCardRepository.save(map(item, context.getUserId(), ActionEnum.DELETE));
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        BankCard row = findById(id, context);
        if (row != null) {
            delete(row, context);
        }
    }

    @Override
    public BankCard findById(UUID id, CoreContext context) {
        BankCard row = bankCardRepository.findById(id).orElse(null);
        if (row != null) {
            ProfileScopeSupport.assertProfileScope(row.getProfileId(), context);
            ProfileScopeSupport.assertAppScope(row.getAppId(), context);
        }
        return row;
    }

    @Override
    public List<BankCard> listByProfile(UUID profileId, UUID appId, CoreContext context) {
        UUID pid = profileId != null ? profileId : ProfileScopeSupport.requireProfileId(context);
        UUID aid = appId != null ? appId : context.getAppId();
        if (aid != null) {
            return bankCardRepository.findByProfileIdAndAppIdAndIsActiveTrueOrderByCreatedAtDesc(pid, aid);
        }
        return bankCardRepository.findByProfileIdAndIsActiveTrueOrderByCreatedAtDesc(pid);
    }

    @Override
    @Transactional
    public void hardDeleteById(UUID id, CoreContext context) {
        BankCard found = findById(id, context);
        if (found != null) {
            bankCardRepository.delete(found);
        }
    }

    @Override
    @Transactional
    public BankCard setActive(UUID id, boolean active, CoreContext context) {
        BankCard found = findById(id, context);
        if (found == null) {
            throw new CoreException("NOT_FOUND", "BankCard not found");
        }
        found.setIsActive(active);
        found = map(found, context.getUserId(), ActionEnum.UPDATE);
        found.setIsActive(active);
        return bankCardRepository.save(found);
    }
}