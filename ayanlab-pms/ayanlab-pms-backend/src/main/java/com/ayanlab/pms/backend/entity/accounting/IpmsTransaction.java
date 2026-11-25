package com.ayanlab.pms.backend.entity.accounting;

import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.pms.backend.dto.OrgScoped;
import com.ayanlab.pms.backend.enums.IpmsTransactionStatusEnum;
import com.ayanlab.pms.backend.enums.IpmsTransactionTypeEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
    name = "IpmsTransaction",
    schema = "ipms",
    indexes = {
        @Index(name = "idx_ipms_txn_org", columnList = "organizationId"),
        @Index(name = "idx_ipms_txn_property", columnList = "propertyId"),
        @Index(name = "idx_ipms_txn_date", columnList = "transactionDate")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IpmsTransaction extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @Column(name = "propertyId")
    private UUID propertyId;

    @Column(name = "financialRecordId")
    private UUID financialRecordId;

    @Column(name = "bankAccountId")
    private UUID bankAccountId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IpmsTransactionTypeEnum type;

    @Column(nullable = false, name = "amountCents")
    private Long amountCents;

    @Column(columnDefinition = "text")
    private String description;

    @Column(nullable = false, name = "transactionDate")
    private LocalDate transactionDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private IpmsTransactionStatusEnum status = IpmsTransactionStatusEnum.PENDING;

    @Column(name = "referenceNumber")
    private String referenceNumber;
}
