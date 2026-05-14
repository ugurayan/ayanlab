package com.ayanlabs.core.entity;

import com.ayanlabs.core.data.enums.SubscriptionStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Subscription instance for an end-user (vault schema).
 */
@Entity
@Table(
    name = "UserSubscription",
    schema = "vault",
    indexes = {
        @Index(name = "idx_user_sub_user_app", columnList = "userId,appId")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSubscription extends BaseEntity {

    @Column(nullable = false, name = "userId")
    private UUID userId;

    /** Optional: which product app this subscription belongs to (matches {@code App.id}). */
    @Column(name = "appId")
    private UUID appId;

    @Column(nullable = false, name = "subscriptionPlanId")
    private UUID subscriptionPlanId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "status")
    private SubscriptionStatusEnum status;

    @Column(nullable = false, name = "startedAt")
    private Instant startedAt;

    @Column(name = "currentPeriodEnd")
    private Instant currentPeriodEnd;

    @Column(name = "cancelledAt")
    private Instant cancelledAt;
}
