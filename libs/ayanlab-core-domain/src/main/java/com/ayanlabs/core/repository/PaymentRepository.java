package com.ayanlabs.core.repository;

import com.ayanlabs.core.data.enums.PaymentStatusEnum;
import com.ayanlabs.core.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByTransactionId(UUID transactionId);

    List<Payment> findByAppIdOrderByCreatedAtDesc(UUID appId);

    List<Payment> findByInvoiceIdOrderByCreatedAtDesc(UUID invoiceId);

    List<Payment> findByAppIdAndStatusOrderByCreatedAtDesc(UUID appId, PaymentStatusEnum status);
}
