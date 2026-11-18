package com.ayanlab.core.entity;

import com.ayanlab.core.data.enums.InvoiceStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Core billing document (subscription, org, or profile scoped).
 */
@Entity
@Table(
    name = "Invoice",
    schema = "core",
    uniqueConstraints = @UniqueConstraint(columnNames = {"appId", "invoiceNumber"}),
    indexes = {
        @Index(name = "idx_invoice_app", columnList = "appId"),
        @Index(name = "idx_invoice_profile", columnList = "profileId"),
        @Index(name = "idx_invoice_org", columnList = "organizationId")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice extends BaseEntity {

    @Column(nullable = false, name = "appId")
    private UUID appId;

    @Column(name = "organizationId")
    private UUID organizationId;

    @Column(name = "profileId")
    private UUID profileId;

    @Column(name = "profileSubscriptionId")
    private UUID profileSubscriptionId;

    @Column(nullable = false, name = "invoiceNumber")
    @NotBlank
    private String invoiceNumber;

    @Column(nullable = false, name = "customerName")
    @NotBlank
    private String customerName;

    @Column(name = "customerEmail")
    private String customerEmail;

    @Column(nullable = false, name = "issueDate")
    private LocalDate issueDate;

    @Column(nullable = false, name = "dueDate")
    private LocalDate dueDate;

    @Column(name = "subtotalCents")
    private Long subtotalCents;

    @Column(name = "taxAmountCents")
    private Long taxAmountCents;

    @Column(nullable = false, name = "totalAmountCents")
    private Long totalAmountCents;

    @Column(name = "currency", length = 8)
    @Builder.Default
    private String currency = "USD";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "status")
    @Builder.Default
    private InvoiceStatusEnum status = InvoiceStatusEnum.DRAFT;

    @Column(columnDefinition = "text")
    private String notes;

    @Column(name = "paidAt")
    private Instant paidAt;

    /** Stripe / App Store / internal payment reference. */
    @Column(name = "externalReference")
    private String externalReference;
}
