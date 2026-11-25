package com.ayanlab.pms.backend.entity.associations;

import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.pms.backend.dto.OrgScoped;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

/** NYC / co-op share registry (Building Stack–class baseline). */
@Entity
@Table(
    name = "CooperativeShareRecord",
    schema = "ipms",
    indexes = @Index(name = "idx_ipms_coop_share_org", columnList = "organizationId")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CooperativeShareRecord extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @Column(name = "associationId")
    private UUID associationId;

    @NotBlank
    @Column(nullable = false, name = "unitLabel")
    private String unitLabel;

    @NotBlank
    @Column(nullable = false, name = "shareholderName")
    private String shareholderName;

    @Column(nullable = false, name = "shareCount")
    @Builder.Default
    private Integer shareCount = 0;

    @Column(name = "totalSharesInBuilding")
    private Integer totalSharesInBuilding;

    @Column(name = "flipTaxCents")
    private Long flipTaxCents;

    @Column(name = "monthlyMaintenanceCents")
    private Long monthlyMaintenanceCents;

    @Column(name = "proprietaryLeaseId")
    private UUID proprietaryLeaseId;

    @Column(nullable = false, name = "boardApprovalRequired")
    @Builder.Default
    private Boolean boardApprovalRequired = true;
}