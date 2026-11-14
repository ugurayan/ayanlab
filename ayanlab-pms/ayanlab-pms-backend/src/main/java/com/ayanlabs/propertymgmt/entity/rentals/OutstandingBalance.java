package com.ayanlabs.propertymgmt.entity.rentals;

import com.ayanlabs.core.entity.BaseEntity;
import com.ayanlabs.propertymgmt.data.OrgScoped;
import com.ayanlabs.propertymgmt.data.enums.OutstandingBalanceStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
    name = "OutstandingBalance",
    schema = "ipms",
    indexes = {
        @Index(name = "idx_ipms_balance_org", columnList = "organizationId"),
        @Index(name = "idx_ipms_balance_lease", columnList = "leaseId")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutstandingBalance extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @Column(name = "leaseId")
    private UUID leaseId;

    @Column(name = "rentalOwnerId")
    private UUID rentalOwnerId;

    @Column(name = "tenantProfileId")
    private UUID tenantProfileId;

    @Column(nullable = false, name = "amountDueCents")
    private Long amountDueCents;

    @Column(name = "amountPaidCents")
    @Builder.Default
    private Long amountPaidCents = 0L;

    @Column(nullable = false, name = "dueDate")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OutstandingBalanceStatusEnum status = OutstandingBalanceStatusEnum.OPEN;

    @Column(columnDefinition = "text")
    private String description;
}
