package com.ayanlabs.core.entity;

import com.ayanlabs.core.data.enums.BillingPeriodEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Lookup / catalog of sellable subscription tiers (stored in {@code vault} schema).
 */
@Entity
@Table(name = "SubscriptionPlan", schema = "vault", uniqueConstraints = @UniqueConstraint(columnNames = {"code"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlan extends BaseEntity {

    @Column(nullable = false, unique = true)
    @NotBlank
    private String code;

    @Column(nullable = false)
    @NotBlank
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "billingPeriod")
    private BillingPeriodEnum billingPeriod;

    @Column(name = "amountCents")
    private Long amountCents;

    @Column(name = "currency", length = 8)
    private String currency;

    @Column(name = "metadataJson", columnDefinition = "text")
    private String metadataJson;

    @Column(nullable = false, name = "catalogActive")
    @Builder.Default
    private Boolean catalogActive = true;
}
