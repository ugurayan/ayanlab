package com.ayanlab.pms.backend.entity.accounting;

import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.pms.backend.data.OrgScoped;
import com.ayanlab.pms.backend.data.enums.BillStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
    name = "Bill",
    schema = "ipms",
    indexes = {
        @Index(name = "idx_ipms_bill_org", columnList = "organizationId"),
        @Index(name = "idx_ipms_bill_vendor", columnList = "vendorId")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bill extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @Column(name = "vendorId")
    private UUID vendorId;

    @Column(name = "propertyId")
    private UUID propertyId;

    @NotBlank
    @Column(nullable = false, name = "billNumber")
    private String billNumber;

    @Column(nullable = false, name = "issueDate")
    private LocalDate issueDate;

    @Column(nullable = false, name = "dueDate")
    private LocalDate dueDate;

    @Column(nullable = false, name = "amountCents")
    private Long amountCents;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BillStatusEnum status = BillStatusEnum.DRAFT;

    @Column(name = "paidAt")
    private Instant paidAt;

    @Column(columnDefinition = "text")
    private String notes;
}
