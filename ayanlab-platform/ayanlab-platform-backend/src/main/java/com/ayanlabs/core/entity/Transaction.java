package com.ayanlabs.core.entity;

import com.ayanlabs.core.data.enums.PaymentProviderEnum;
import com.ayanlabs.core.data.enums.TransactionStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

/**
 * Idempotent money-movement record from a payment provider (core schema).
 */
@Entity
@Table(
    name = "Transaction",
    schema = "core",
    uniqueConstraints = @UniqueConstraint(columnNames = {"appId", "idempotencyKey"}),
    indexes = {
        @Index(name = "idx_transaction_app", columnList = "appId"),
        @Index(name = "idx_transaction_profile", columnList = "profileId"),
        @Index(name = "idx_transaction_status", columnList = "appId,status")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction extends BaseEntity {

    @Column(nullable = false, name = "appId")
    private UUID appId;

    @Column(nullable = false, name = "idempotencyKey")
    @NotBlank
    private String idempotencyKey;

    @Column(name = "profileId")
    private UUID profileId;

    @Column(name = "organizationId")
    private UUID organizationId;

    @Column(name = "profileSubscriptionId")
    private UUID profileSubscriptionId;

    @Column(nullable = false, name = "amountCents")
    private Long amountCents;

    @Column(name = "currency", length = 8)
    @Builder.Default
    private String currency = "USD";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "status")
    @Builder.Default
    private TransactionStatusEnum status = TransactionStatusEnum.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "provider")
    private PaymentProviderEnum provider;

    @Column(name = "externalTransactionId")
    private String externalTransactionId;

    @Column(columnDefinition = "text")
    private String failureReason;
}
