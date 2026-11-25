package com.ayanlab.pms.backend.entity.leasing;

import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.pms.backend.dto.OrgScoped;
import com.ayanlab.pms.backend.enums.LeaseTypeEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
    name = "DraftLease",
    schema = "ipms",
    indexes = {
        @Index(name = "idx_ipms_draft_lease_org", columnList = "organizationId"),
        @Index(name = "idx_ipms_draft_lease_unit", columnList = "unitId")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DraftLease extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @Column(nullable = false, name = "propertyId")
    private UUID propertyId;

    @Column(nullable = false, name = "unitId")
    private UUID unitId;

    @Column(name = "tenantProfileId")
    private UUID tenantProfileId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "leaseType")
    @Builder.Default
    private LeaseTypeEnum leaseType = LeaseTypeEnum.FIXED;

    @Column(name = "startDate")
    private LocalDate startDate;

    @Column(name = "endDate")
    private LocalDate endDate;

    @Column(name = "monthlyRentCents")
    private Long monthlyRentCents;

    @Column(name = "securityDepositCents")
    private Long securityDepositCents;

    @Column(name = "templateData", columnDefinition = "text")
    private String templateData;

    @Column(name = "lastEditedAt")
    private Instant lastEditedAt;
}
