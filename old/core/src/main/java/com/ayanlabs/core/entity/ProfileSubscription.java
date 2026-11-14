package com.ayanlabs.core.entity;

import com.ayanlabs.core.data.enums.SubscriptionStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Active or historical subscription bound to a {@link UserProfile}.
 */
@Entity
@Table(
    name = "ProfileSubscription",
    schema = "core",
    indexes = {
        @Index(name = "idx_profile_sub_profile", columnList = "profileId"),
        @Index(name = "idx_profile_sub_status", columnList = "profileId,status")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileSubscription extends BaseEntity {

    @Column(nullable = false, name = "profileId")
    private UUID profileId;

    @Column(nullable = false, name = "subscriptionPlanId")
    private UUID subscriptionPlanId;

    /** Null for complimentary / free tiers without a priced SKU. */
    @Column(name = "subscriptionPlanPriceId")
    private UUID subscriptionPlanPriceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "status")
    private SubscriptionStatusEnum status;

    @Column(nullable = false, name = "startedAt")
    private Instant startedAt;

    @Column(name = "currentPeriodStart")
    private Instant currentPeriodStart;

    @Column(name = "currentPeriodEnd")
    private Instant currentPeriodEnd;

    @Column(name = "trialEndsAt")
    private Instant trialEndsAt;

    @Column(name = "cancelledAt")
    private Instant cancelledAt;

    @Column(name = "autoRenew")
    @Builder.Default
    private Boolean autoRenew = true;

    /** App Store / Play / Stripe reference for server-side verification (future). */
    @Column(name = "externalSubscriptionId")
    private String externalSubscriptionId;
}
