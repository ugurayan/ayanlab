package com.ayanlab.core.service;

import com.ayanlab.core.data.CoreContext;
import com.ayanlab.core.entity.BankAccount;

import java.util.List;
import java.util.UUID;

public interface IBankAccountService extends IBaseService<BankAccount> {
    List<BankAccount> listByProfile(UUID profileId, UUID appId, CoreContext context);
}