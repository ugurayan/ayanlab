package com.ayanlab.pms.backend.entity.leasing;

import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.pms.backend.dto.OrgScoped;
import com.ayanlab.pms.backend.enums.RentersInsuranceStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
    name = "RentersInsurancePolicy",
    schema = "ipms",
    indexes = {
        @Index(name = "idx_ipms_renters_ins_org", columnList = "organizationId"),
        @Index(name = "idx_ipms_renters_ins_tenant", columnList = "tenantProfileId"),
        @Index(name = "idx_ipms_renters_ins_lease", columnList = "leaseId")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentersInsurancePolicy extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @Column(name = "leaseId")
    private UUID leaseId;

    @Column(name = "tenantProfileId")
    private UUID tenantProfileId;

    @Column(name = "propertyId")
    private UUID propertyId;

    @Column(name = "unitId")
    private UUID unitId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RentersInsuranceStatusEnum status = RentersInsuranceStatusEnum.UNINSURED;

    @Column(name = "carrierName")
    private String carrierName;

    @Column(name = "policyNumber")
    private String policyNumber;

    @Column(name = "effectiveDate")
    private LocalDate effectiveDate;

    @Column(name = "expirationDate")
    private LocalDate expirationDate;

    /** Compliance deadline when still uninsured / pending. */
    @Column(name = "requiredDeadline")
    private LocalDate requiredDeadline;

    @Column(name = "documentFileId")
    private UUID documentFileId;

    @Column(columnDefinition = "text")
    private String notes;
}
