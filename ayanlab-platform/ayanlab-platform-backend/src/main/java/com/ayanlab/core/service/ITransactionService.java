package com.ayanlab.core.service;

import com.ayanlab.core.enums.TransactionStatusEnum;
import com.ayanlab.core.entity.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ITransactionService extends IBaseService<Transaction> {

    Optional<Transaction> findByIdempotencyKey(UUID appId, String idempotencyKey);

    List<Transaction> listByApp(UUID appId, TransactionStatusEnum status);

    List<Transaction> listByProfile(UUID profileId);
}
