package com.ayanlabs.core.service;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.entity.BankAccount;

import java.util.List;
import java.util.UUID;

public interface IBankAccountService extends IBaseService<BankAccount> {
    List<BankAccount> listByProfile(UUID profileId, UUID appId, CoreContext context);
}