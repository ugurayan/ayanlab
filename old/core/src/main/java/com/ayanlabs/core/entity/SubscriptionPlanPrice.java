package com.ayanlabs.core.entity;

import com.ayanlabs.core.data.enums.BillingPeriodEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Billing variant for a plan (monthly, yearly, lifetime) with storefront SKU hooks.
 */
@Entity
@Table(
    name = "SubscriptionPlanPrice",
    schema = "core",
    uniqueConstraints = @UniqueConstraint(columnNames = {"subscriptionPlanId", "billingPeriod"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlanPrice extends BaseEntity {

    @Column(nullable = false, name = "subscriptionPlanId")
    private UUID subscriptionPlanId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "billingPeriod")
    private BillingPeriodEnum billingPeriod;

    /** Charged amount in minor units (cents). */
    @Column(name = "amountCents")
    private Long amountCents;

    /** Optional list / strike-through price for promotions. */
    @Column(name = "listAmountCents")
    private Long listAmountCents;

    @Column(name = "currency", length = 8)
    @Builder.Default
    private String currency = "USD";

    @Column(name = "appleStoreProductId")
    private String appleStoreProductId;

    @Column(name = "googlePlayProductId")
    private String googlePlayProductId;

    @Column(nullable = false, name = "catalogActive")
    @Builder.Default
    private Boolean catalogActive = true;
}
