package com.ayanlabs.core.service.impl;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.enums.ActionEnum;
import com.ayanlabs.core.data.enums.TransactionStatusEnum;
import com.ayanlabs.core.entity.Transaction;
import com.ayanlabs.core.exception.CoreException;
import com.ayanlabs.core.repository.TransactionRepository;
import com.ayanlabs.core.service.ITransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.ayanlabs.core.data.mapper.BaseMapper.map;

@Service
@RequiredArgsConstructor
public class TransactionService implements ITransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public Transaction create(Transaction item, CoreContext context) {
        if (item.getAppId() == null) {
            item.setAppId(context.getAppId());
        }
        assertAppScope(item.getAppId(), context);
        assertUniqueIdempotencyKey(item.getAppId(), item.getIdempotencyKey(), null);
        item = map(item, context.getUserId(), ActionEnum.CREATE);
        return transactionRepository.save(item);
    }

    @Override
    @Transactional
    public Transaction update(Transaction item, CoreContext context) {
        if (item.getId() == null) {
            throw new CoreException("INVALID_ARGUMENT", "Transaction id required");
        }
        Transaction current = transactionRepository.findById(item.getId())
            .orElseThrow(() -> new CoreException("NOT_FOUND", "Transaction not found"));
        assertAppScope(current.getAppId(), context);
        if (item.getAppId() == null) {
            item.setAppId(current.getAppId());
        }
        if (item.getIdempotencyKey() != null && !item.getIdempotencyKey().equals(current.getIdempotencyKey())) {
            assertUniqueIdempotencyKey(item.getAppId(), item.getIdempotencyKey(), item.getId());
        }
        item = map(item, context.getUserId(), ActionEnum.UPDATE);
        return transactionRepository.save(item);
    }

    @Override
    @Transactional
    public void delete(Transaction item, CoreContext context) {
        item.setIsActive(false);
        item = map(item, context.getUserId(), ActionEnum.DELETE);
        transactionRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        Transaction row = findById(id, context);
        if (row != null) {
            delete(row, context);
        }
    }

    @Override
    public Transaction findById(UUID id, CoreContext context) {
        Transaction row = transactionRepository.findById(id).orElse(null);
        if (row == null) {
            return null;
        }
        assertAppScope(row.getAppId(), context);
        return row;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Transaction> findByIdempotencyKey(UUID appId, String idempotencyKey) {
        return transactionRepository.findByAppIdAndIdempotencyKey(appId, idempotencyKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> listByApp(UUID appId, TransactionStatusEnum status) {
        if (status == null) {
            return transactionRepository.findByAppIdOrderByCreatedAtDesc(appId);
        }
        return transactionRepository.findByAppIdAndStatusOrderByCreatedAtDesc(appId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> listByProfile(UUID profileId) {
        return transactionRepository.findByProfileIdOrderByCreatedAtDesc(profileId);
    }

    private void assertAppScope(UUID appId, CoreContext context) {
        if (context.getAppId() != null && appId != null && !context.getAppId().equals(appId)) {
            throw new CoreException("FORBIDDEN", "Transaction does not belong to the current app");
        }
    }

    private void assertUniqueIdempotencyKey(UUID appId, String idempotencyKey, UUID excludeId) {
        transactionRepository.findByAppIdAndIdempotencyKey(appId, idempotencyKey).ifPresent(existing -> {
            if (excludeId == null || !existing.getId().equals(excludeId)) {
                throw new CoreException("CONFLICT", "Transaction idempotency key already exists for this app");
            }
        });
    }
}
