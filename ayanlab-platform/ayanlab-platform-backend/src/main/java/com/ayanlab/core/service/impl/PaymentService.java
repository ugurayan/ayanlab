package com.ayanlab.core.service.impl;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.enums.ActionEnum;
import com.ayanlab.core.enums.InvoiceStatusEnum;
import com.ayanlab.core.enums.PaymentStatusEnum;
import com.ayanlab.core.entity.Invoice;
import com.ayanlab.core.entity.Payment;
import com.ayanlab.core.entity.Transaction;
import com.ayanlab.core.exception.CoreException;
import com.ayanlab.core.repository.InvoiceRepository;
import com.ayanlab.core.repository.PaymentRepository;
import com.ayanlab.core.repository.TransactionRepository;
import com.ayanlab.core.service.IPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.ayanlab.core.mapping.BaseMapper.map;

@Service
@RequiredArgsConstructor
public class PaymentService implements IPaymentService {

    private final PaymentRepository paymentRepository;
    private final TransactionRepository transactionRepository;
    private final InvoiceRepository invoiceRepository;

    @Override
    @Transactional
    public Payment create(Payment item, CoreContext context) {
        if (item.getAppId() == null) {
            item.setAppId(context.getAppId());
        }
        if (item.getProfileId() == null && context.getProfileId() != null) {
            item.setProfileId(context.getProfileId());
        }
        if (item.getProfileId() == null) {
            throw new CoreException("VALIDATION_ERROR", "profileId required (body or X-Profile-Id)");
        }
        assertAppScope(item.getAppId(), context);
        Transaction transaction = resolveTransaction(item.getTransactionId(), item.getAppId());
        assertSinglePaymentPerTransaction(item.getTransactionId(), null);
        if (item.getInvoiceId() != null) {
            resolveInvoice(item.getInvoiceId(), item.getAppId());
        }
        if (item.getCurrency() == null) {
            item.setCurrency(transaction.getCurrency());
        }
        item = map(item, context.getUserId(), ActionEnum.CREATE);
        Payment saved = paymentRepository.save(item);
        maybeMarkInvoicePaid(saved);
        return saved;
    }

    @Override
    @Transactional
    public Payment update(Payment item, CoreContext context) {
        if (item.getId() == null) {
            throw new CoreException("INVALID_ARGUMENT", "Payment id required");
        }
        Payment current = paymentRepository.findById(item.getId())
            .orElseThrow(() -> new CoreException("NOT_FOUND", "Payment not found"));
        assertAppScope(current.getAppId(), context);
        if (item.getAppId() == null) {
            item.setAppId(current.getAppId());
        }
        UUID transactionId = item.getTransactionId() != null ? item.getTransactionId() : current.getTransactionId();
        if (item.getTransactionId() != null && !item.getTransactionId().equals(current.getTransactionId())) {
            resolveTransaction(transactionId, item.getAppId());
            assertSinglePaymentPerTransaction(transactionId, item.getId());
        }
        if (item.getInvoiceId() != null) {
            resolveInvoice(item.getInvoiceId(), item.getAppId());
        }
        item = map(item, context.getUserId(), ActionEnum.UPDATE);
        Payment saved = paymentRepository.save(item);
        maybeMarkInvoicePaid(saved);
        return saved;
    }

    @Override
    @Transactional
    public void delete(Payment item, CoreContext context) {
        item.setIsActive(false);
        item = map(item, context.getUserId(), ActionEnum.DELETE);
        paymentRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        Payment row = findById(id, context);
        if (row != null) {
            delete(row, context);
        }
    }

    @Override
    public Payment findById(UUID id, CoreContext context) {
        Payment row = paymentRepository.findById(id).orElse(null);
        if (row == null) {
            return null;
        }
        assertAppScope(row.getAppId(), context);
        return row;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Payment> findByTransactionId(UUID transactionId) {
        return paymentRepository.findByTransactionId(transactionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Payment> listByApp(UUID appId, PaymentStatusEnum status) {
        if (status == null) {
            return paymentRepository.findByAppIdOrderByCreatedAtDesc(appId);
        }
        return paymentRepository.findByAppIdAndStatusOrderByCreatedAtDesc(appId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Payment> listByInvoice(UUID invoiceId) {
        return paymentRepository.findByInvoiceIdOrderByCreatedAtDesc(invoiceId);
    }

    private Transaction resolveTransaction(UUID transactionId, UUID appId) {
        Transaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new CoreException("NOT_FOUND", "Transaction not found"));
        if (!transaction.getAppId().equals(appId)) {
            throw new CoreException("INVALID_ARGUMENT", "Transaction does not belong to the payment app");
        }
        return transaction;
    }

    private Invoice resolveInvoice(UUID invoiceId, UUID appId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new CoreException("NOT_FOUND", "Invoice not found"));
        if (!invoice.getAppId().equals(appId)) {
            throw new CoreException("INVALID_ARGUMENT", "Invoice does not belong to the payment app");
        }
        return invoice;
    }

    private void assertSinglePaymentPerTransaction(UUID transactionId, UUID excludePaymentId) {
        paymentRepository.findByTransactionId(transactionId).ifPresent(existing -> {
            if (excludePaymentId == null || !existing.getId().equals(excludePaymentId)) {
                throw new CoreException("CONFLICT", "A payment already exists for this transaction");
            }
        });
    }

    private void maybeMarkInvoicePaid(Payment payment) {
        if (payment.getInvoiceId() == null || payment.getStatus() != PaymentStatusEnum.SUCCEEDED) {
            return;
        }
        invoiceRepository.findById(payment.getInvoiceId()).ifPresent(invoice -> {
            if (invoice.getStatus() != InvoiceStatusEnum.PAID) {
                invoice.setStatus(InvoiceStatusEnum.PAID);
                invoice.setPaidAt(payment.getPaidAt() != null ? payment.getPaidAt() : Instant.now());
                invoiceRepository.save(invoice);
            }
        });
    }

    private void assertAppScope(UUID appId, CoreContext context) {
        if (context.getAppId() != null && appId != null && !context.getAppId().equals(appId)) {
            throw new CoreException("FORBIDDEN", "Payment does not belong to the current app");
        }
    }

    @Override
    @Transactional
    public void hardDeleteById(UUID id, CoreContext context) {
        Payment found = findById(id, context);
        if (found != null) {
            paymentRepository.delete(found);
        }
    }

    @Override
    @Transactional
    public Payment setActive(UUID id, boolean active, CoreContext context) {
        Payment found = findById(id, context);
        if (found == null) {
            throw new CoreException("NOT_FOUND", "Payment not found");
        }
        found.setIsActive(active);
        found = map(found, context.getUserId(), ActionEnum.UPDATE);
        found.setIsActive(active);
        return paymentRepository.save(found);
    }
}
