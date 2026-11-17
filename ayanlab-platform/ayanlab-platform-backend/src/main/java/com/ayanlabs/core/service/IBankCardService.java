package com.ayanlabs.core.service;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.entity.BankCard;

import java.util.List;
import java.util.UUID;

public interface IBankCardService extends IBaseService<BankCard> {
    List<BankCard> listByProfile(UUID profileId, UUID appId, CoreContext context);
}