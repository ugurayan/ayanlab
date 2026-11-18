package com.ayanlab.pms.backend.entity.associations;

import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.pms.backend.data.OrgScoped;
import com.ayanlab.pms.backend.data.enums.ViolationStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
    name = "Violation",
    schema = "ipms",
    indexes = {
        @Index(name = "idx_ipms_violation_org", columnList = "organizationId"),
        @Index(name = "idx_ipms_violation_assoc", columnList = "associationId")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Violation extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @Column(nullable = false, name = "associationId")
    private UUID associationId;

    @Column(name = "ownershipAccountId")
    private UUID ownershipAccountId;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Column(nullable = false, name = "violationDate")
    private LocalDate violationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ViolationStatusEnum status = ViolationStatusEnum.OPEN;

    @Column(name = "fineAmountCents")
    private Long fineAmountCents;

    @Column(name = "resolvedAt")
    private Instant resolvedAt;
}
