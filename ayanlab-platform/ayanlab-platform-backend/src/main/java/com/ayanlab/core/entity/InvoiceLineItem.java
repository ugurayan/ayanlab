package com.ayanlab.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(
    name = "InvoiceLineItem",
    schema = "core",
    indexes = @Index(name = "idx_invoice_line_invoice", columnList = "invoiceId")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceLineItem extends BaseEntity {

    @Column(nullable = false, name = "invoiceId")
    private UUID invoiceId;

    @Column(nullable = false, columnDefinition = "text")
    @NotBlank
    private String description;

    @Column(nullable = false, precision = 12, scale = 4)
    @Builder.Default
    private BigDecimal quantity = BigDecimal.ONE;

    @Column(nullable = false, name = "unitAmountCents")
    private Long unitAmountCents;

    @Column(nullable = false, name = "amountCents")
    private Long amountCents;

    @Column(name = "sortOrder")
    @Builder.Default
    private Integer sortOrder = 0;
}
