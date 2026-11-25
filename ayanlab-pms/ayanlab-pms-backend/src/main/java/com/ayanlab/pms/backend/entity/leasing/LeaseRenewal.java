package com.ayanlab.pms.backend.entity.leasing;

import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.pms.backend.dto.OrgScoped;
import com.ayanlab.pms.backend.enums.LeaseRenewalStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
    name = "LeaseRenewal",
    schema = "ipms",
    indexes = {
        @Index(name = "idx_ipms_renewal_org", columnList = "organizationId"),
        @Index(name = "idx_ipms_renewal_lease", columnList = "leaseId")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaseRenewal extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @Column(nullable = false, name = "leaseId")
    private UUID leaseId;

    @Column(nullable = false, name = "proposedStartDate")
    private LocalDate proposedStartDate;

    @Column(name = "proposedEndDate")
    private LocalDate proposedEndDate;

    @Column(nullable = false, name = "proposedRentCents")
    private Long proposedRentCents;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private LeaseRenewalStatusEnum status = LeaseRenewalStatusEnum.PENDING;

    @Column(name = "offeredAt")
    private Instant offeredAt;

    @Column(name = "respondedAt")
    private Instant respondedAt;
}
