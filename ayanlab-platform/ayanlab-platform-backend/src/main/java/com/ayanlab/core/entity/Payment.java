package com.ayanlab.core.entity;

import com.ayanlab.core.data.enums.PaymentMethodEnum;
import com.ayanlab.core.data.enums.PaymentStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Application of a {@link Transaction} to an {@link Invoice} or subscription (core schema).
 */
@Entity
@Table(
    name = "Payment",
    schema = "core",
    indexes = {
        @Index(name = "idx_payment_app", columnList = "appId"),
        @Index(name = "idx_payment_invoice", columnList = "invoiceId"),
        @Index(name = "idx_payment_transaction", columnList = "transactionId")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends BaseEntity {

    @Column(nullable = false, name = "appId")
    private UUID appId;

    @Column(nullable = false, name = "transactionId")
    private UUID transactionId;

    @Column(name = "invoiceId")
    private UUID invoiceId;

    @Column(name = "profileSubscriptionId")
    private UUID profileSubscriptionId;

    @Column(name = "profileId")
    private UUID profileId;

    @Column(nullable = false, name = "amountCents")
    private Long amountCents;

    @Column(name = "currency", length = 8)
    @Builder.Default
    private String currency = "USD";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "status")
    @Builder.Default
    private PaymentStatusEnum status = PaymentStatusEnum.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "paymentMethod")
    private PaymentMethodEnum paymentMethod;

    @Column(name = "paidAt")
    private Instant paidAt;

    @Column(name = "externalPaymentId")
    private String externalPaymentId;
}
