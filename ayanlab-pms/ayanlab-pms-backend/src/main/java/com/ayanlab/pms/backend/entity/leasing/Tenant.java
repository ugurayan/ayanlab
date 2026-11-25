package com.ayanlab.pms.backend.entity.leasing;

import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.pms.backend.dto.OrgScoped;
import com.ayanlab.pms.backend.enums.TenantStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
    name = "Tenant",
    schema = "ipms",
    indexes = {
        @Index(name = "idx_ipms_tenant_org", columnList = "organizationId"),
        @Index(name = "idx_ipms_tenant_unit", columnList = "unitId"),
        @Index(name = "idx_ipms_tenant_lease", columnList = "leaseId"),
        @Index(name = "idx_ipms_tenant_profile", columnList = "profileId")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tenant extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    /** Core profile when tenant has portal access. */
    @Column(name = "profileId")
    private UUID profileId;

    @Column(name = "propertyId")
    private UUID propertyId;

    @Column(name = "unitId")
    private UUID unitId;

    @Column(name = "leaseId")
    private UUID leaseId;

    @NotBlank
    @Column(nullable = false, name = "firstName")
    private String firstName;

    @NotBlank
    @Column(nullable = false, name = "lastName")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "companyName")
    private String companyName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TenantStatusEnum status = TenantStatusEnum.PROSPECTIVE;

    @Column(name = "moveInDate")
    private LocalDate moveInDate;

    @Column(name = "moveOutDate")
    private LocalDate moveOutDate;

    @Column(name = "emergencyContactName")
    private String emergencyContactName;

    @Column(name = "emergencyContactPhone")
    private String emergencyContactPhone;

    @Column(columnDefinition = "text")
    private String notes;
}
