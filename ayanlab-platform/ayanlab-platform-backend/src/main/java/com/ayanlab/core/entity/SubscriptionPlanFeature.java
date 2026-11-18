package com.ayanlab.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

/** Marketing / entitlement feature bullet for a {@link SubscriptionPlan}. */
@Entity
@Table(
    name = "SubscriptionPlanFeature",
    schema = "core",
    indexes = @Index(name = "idx_sub_plan_feature_plan", columnList = "subscriptionPlanId")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlanFeature extends BaseEntity {

    @Column(nullable = false, name = "subscriptionPlanId")
    private UUID subscriptionPlanId;

    @Column(nullable = false, name = "featureCode")
    @NotBlank
    private String featureCode;

    @Column(nullable = false)
    @NotBlank
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "sortOrder")
    @Builder.Default
    private Integer sortOrder = 0;

    /** When false, feature is listed but not included (comparison tables). */
    @Column(nullable = false, name = "included")
    @Builder.Default
    private Boolean included = true;
}
