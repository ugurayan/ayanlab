package com.ayanlab.pms.backend.entity.associations;

import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.pms.backend.data.OrgScoped;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
    name = "OwnershipAccount",
    schema = "ipms",
    uniqueConstraints = @UniqueConstraint(columnNames = {"associationId", "accountNumber"}),
    indexes = {
        @Index(name = "idx_ipms_ownership_org", columnList = "organizationId"),
        @Index(name = "idx_ipms_ownership_assoc", columnList = "associationId")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnershipAccount extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @Column(nullable = false, name = "associationId")
    private UUID associationId;

    @Column(name = "profileId")
    private UUID profileId;

    @Column(name = "unitId")
    private UUID unitId;

    @NotBlank
    @Column(nullable = false, name = "accountNumber")
    private String accountNumber;

    @Column(name = "ownershipPercentage")
    private BigDecimal ownershipPercentage;

    @Column(name = "startDate")
    private LocalDate startDate;

    @Column(name = "endDate")
    private LocalDate endDate;
}
