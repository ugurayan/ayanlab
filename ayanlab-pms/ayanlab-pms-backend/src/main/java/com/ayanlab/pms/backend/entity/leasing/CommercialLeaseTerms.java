package com.ayanlab.pms.backend.entity.leasing;

import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.pms.backend.data.OrgScoped;
import com.ayanlab.pms.backend.data.enums.CommercialLeaseStructureEnum;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(
    name = "CommercialLeaseTerms",
    schema = "ipms",
    indexes = {
        @Index(name = "idx_ipms_comm_lease_org", columnList = "organizationId"),
        @Index(name = "idx_ipms_comm_lease_lease", columnList = "leaseId", unique = true)
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommercialLeaseTerms extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @Column(nullable = false, name = "leaseId", unique = true)
    private UUID leaseId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "leaseStructure")
    private CommercialLeaseStructureEnum leaseStructure;

    @Column(name = "baseRentCents")
    private Long baseRentCents;

    @Column(name = "camCents")
    private Long camCents;

    @Column(name = "taxPassThroughCents")
    private Long taxPassThroughCents;

    @Column(name = "insurancePassThroughCents")
    private Long insurancePassThroughCents;

    @Column(name = "percentageRentThresholdCents")
    private Long percentageRentThresholdCents;

    @Column(name = "percentageRentBps")
    private Integer percentageRentBps;

    @Column(name = "usableSquareFeet")
    private BigDecimal usableSquareFeet;

    @Column(name = "tenantImprovementAllowanceCents")
    private Long tenantImprovementAllowanceCents;
}