package com.ayanlab.pms.backend.entity.leasing;

import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.pms.backend.dto.OrgScoped;
import com.ayanlab.pms.backend.enums.LeaseStatusEnum;
import com.ayanlab.pms.backend.enums.LeaseTypeEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
    name = "Lease",
    schema = "ipms",
    indexes = {
        @Index(name = "idx_ipms_lease_org", columnList = "organizationId"),
        @Index(name = "idx_ipms_lease_unit", columnList = "unitId"),
        @Index(name = "idx_ipms_lease_tenant", columnList = "tenantProfileId")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lease extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @Column(nullable = false, name = "propertyId")
    private UUID propertyId;

    @Column(nullable = false, name = "unitId")
    private UUID unitId;

    @Column(nullable = false, name = "tenantProfileId")
    private UUID tenantProfileId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "leaseType")
    @Builder.Default
    private LeaseTypeEnum leaseType = LeaseTypeEnum.FIXED;

    @Column(nullable = false, name = "startDate")
    private LocalDate startDate;

    @Column(name = "endDate")
    private LocalDate endDate;

    @Column(nullable = false, name = "monthlyRentCents")
    private Long monthlyRentCents;

    @Column(name = "securityDepositCents")
    private Long securityDepositCents;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private LeaseStatusEnum status = LeaseStatusEnum.PENDING;

    @Column(name = "signedAt")
    private Instant signedAt;

    @Column(name = "documentUrl")
    private String documentUrl;
}
