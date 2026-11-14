package com.ayanlabs.core.service;

import com.ayanlabs.core.data.enums.PaymentStatusEnum;
import com.ayanlabs.core.entity.Payment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IPaymentService extends IBaseService<Payment> {

    Optional<Payment> findByTransactionId(UUID transactionId);

    List<Payment> listByApp(UUID appId, PaymentStatusEnum status);

    List<Payment> listByInvoice(UUID invoiceId);
}
