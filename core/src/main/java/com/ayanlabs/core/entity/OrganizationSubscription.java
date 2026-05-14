package com.ayanlabs.core.entity;

import com.ayanlabs.core.data.enums.SubscriptionStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Subscription instance for an organization (vault schema).
 */
@Entity
@Table(
    name = "OrganizationSubscription",
    schema = "vault",
    indexes = {
        @Index(name = "idx_org_sub_org_app", columnList = "organizationId,appId")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationSubscription extends BaseEntity {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

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
