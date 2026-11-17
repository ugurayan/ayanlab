package com.ayanlabs.core.entity;

import com.ayanlabs.core.data.enums.SubscriptionStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/** Organization-level subscription instance (core schema). */
@Entity
@Table(
    name = "OrganizationSubscription",
    schema = "core",
    indexes = @Index(name = "idx_org_sub_org_app", columnList = "organizationId,appId")
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

    @Column(name = "externalSubscriptionId")
    private String externalSubscriptionId;
}
