package com.ayanlab.core.entity;

import com.ayanlab.core.enums.SubscriptionPricingUnitEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

/**
 * Sellable subscription tier catalog row, scoped to an {@link App} (core schema).
 * Monthly / yearly amounts live on {@link SubscriptionPlanPrice}.
 */
@Entity
@Table(
    name = "SubscriptionPlan",
    schema = "core",
    uniqueConstraints = @UniqueConstraint(columnNames = {"appId", "code"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlan extends BaseEntity {

    @Column(nullable = false, name = "appId")
    private UUID appId;

    @Column(nullable = false)
    @NotBlank
    private String code;

    @Column(nullable = false)
    @NotBlank
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    /** Lower sorts first in UI (e.g. Free=0, Individual=10, Families=20). */
    @Column(name = "sortOrder")
    @Builder.Default
    private Integer sortOrder = 0;

    @Column(nullable = false, name = "isFree")
    @Builder.Default
    private Boolean isFree = false;

    /** Optional parent plan whose features are implicitly included (e.g. Families → Individual). */
    @Column(name = "inheritsFromPlanId")
    private UUID inheritsFromPlanId;

    @Enumerated(EnumType.STRING)
    @Column(name = "pricingUnit")
    @Builder.Default
    private SubscriptionPricingUnitEnum pricingUnit = SubscriptionPricingUnitEnum.FLAT;

    /** Max seats for team/family plans; null = unlimited / not applicable. */
    @Column(name = "maxSeats")
    private Integer maxSeats;

    @Column(name = "trialDays")
    @Builder.Default
    private Integer trialDays = 0;

    @Column(nullable = false, name = "catalogActive")
    @Builder.Default
    private Boolean catalogActive = true;
}
