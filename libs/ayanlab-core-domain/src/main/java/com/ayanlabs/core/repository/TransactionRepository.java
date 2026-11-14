package com.ayanlabs.core.repository;

import com.ayanlabs.core.data.enums.TransactionStatusEnum;
import com.ayanlabs.core.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    Optional<Transaction> findByAppIdAndIdempotencyKey(UUID appId, String idempotencyKey);

    List<Transaction> findByAppIdOrderByCreatedAtDesc(UUID appId);

    List<Transaction> findByProfileIdOrderByCreatedAtDesc(UUID profileId);

    List<Transaction> findByAppIdAndStatusOrderByCreatedAtDesc(UUID appId, TransactionStatusEnum status);
}
