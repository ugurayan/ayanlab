package com.ayanlab.pms.backend.entity.accounting;

import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.pms.backend.dto.OrgScoped;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
    name = "FinancialRecord",
    schema = "ipms",
    indexes = {
        @Index(name = "idx_ipms_financial_org", columnList = "organizationId"),
        @Index(name = "idx_ipms_financial_property", columnList = "propertyId")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialRecord extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @Column(name = "propertyId")
    private UUID propertyId;

    @Column(nullable = false, name = "periodStart")
    private LocalDate periodStart;

    @Column(nullable = false, name = "periodEnd")
    private LocalDate periodEnd;

    @Column(name = "totalIncomeCents")
    @Builder.Default
    private Long totalIncomeCents = 0L;

    @Column(name = "totalExpenseCents")
    @Builder.Default
    private Long totalExpenseCents = 0L;

    @Column(name = "netIncomeCents")
    @Builder.Default
    private Long netIncomeCents = 0L;

    @Column(columnDefinition = "text")
    private String notes;
}
