package com.ayanlab.pms.backend.entity.rentals;

import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.pms.backend.dto.OrgScoped;
import com.ayanlab.pms.backend.enums.UnitStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(
    name = "PropertyUnit",
    schema = "ipms",
    uniqueConstraints = @UniqueConstraint(columnNames = {"propertyId", "unitNumber"}),
    indexes = {
        @Index(name = "idx_ipms_unit_org", columnList = "organizationId"),
        @Index(name = "idx_ipms_unit_property", columnList = "propertyId")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyUnit extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @Column(nullable = false, name = "propertyId")
    private UUID propertyId;

    @NotBlank
    @Column(nullable = false, name = "unitNumber")
    private String unitNumber;

    @Column(name = "floorNumber")
    private Integer floorNumber;

    @Column(name = "bedrooms")
    @Builder.Default
    private Integer bedrooms = 1;

    @Column(name = "bathrooms")
    @Builder.Default
    private BigDecimal bathrooms = BigDecimal.ONE;

    @Column(name = "squareFootage")
    private BigDecimal squareFootage;

    @Column(name = "rentAmountCents")
    private Long rentAmountCents;

    @Column(name = "depositAmountCents")
    private Long depositAmountCents;

    /** Unit-level GL mapping (Buildium-style unit accounting). */
    @Column(name = "incomeAccountCode", length = 32)
    private String incomeAccountCode;

    @Column(name = "expenseAccountCode", length = 32)
    private String expenseAccountCode;

    @Column(name = "reserveAccountCode", length = 32)
    private String reserveAccountCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UnitStatusEnum status = UnitStatusEnum.VACANT;
}
