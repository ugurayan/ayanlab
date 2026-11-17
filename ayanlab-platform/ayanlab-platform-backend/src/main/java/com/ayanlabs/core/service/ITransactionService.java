package com.ayanlabs.core.service;

import com.ayanlabs.core.data.enums.TransactionStatusEnum;
import com.ayanlabs.core.entity.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ITransactionService extends IBaseService<Transaction> {

    Optional<Transaction> findByIdempotencyKey(UUID appId, String idempotencyKey);

    List<Transaction> listByApp(UUID appId, TransactionStatusEnum status);

    List<Transaction> listByProfile(UUID profileId);
}
