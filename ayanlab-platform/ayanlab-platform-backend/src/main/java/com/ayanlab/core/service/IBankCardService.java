package com.ayanlab.core.service;

import com.ayanlab.core.data.CoreContext;
import com.ayanlab.core.entity.BankCard;

import java.util.List;
import java.util.UUID;

public interface IBankCardService extends IBaseService<BankCard> {
    List<BankCard> listByProfile(UUID profileId, UUID appId, CoreContext context);
}