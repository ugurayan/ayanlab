package com.ayanlab.pms.backend.entity.tr;

import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.pms.backend.data.OrgScoped;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "LatePenaltyRule", schema = "ipms", indexes = @Index(name = "idx_ipms_late_penalty_org", columnList = "organizationId"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LatePenaltyRule extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @NotBlank
    @Column(nullable = false)
    private String name;

    /** Basis points per month (150 = 1.5%). */
    @Column(nullable = false, name = "rateBps")
    @Builder.Default
    private Integer rateBps = 150;

    @JsonProperty("ratePercentMonthly")
    public double getRatePercentMonthly() {
        return rateBps == null ? 0 : rateBps / 100.0;
    }

    @Column(nullable = false, name = "graceDays")
    @Builder.Default
    private Integer graceDays = 5;

    @Column(nullable = false)
    @Builder.Default
    private Boolean compound = false;
}