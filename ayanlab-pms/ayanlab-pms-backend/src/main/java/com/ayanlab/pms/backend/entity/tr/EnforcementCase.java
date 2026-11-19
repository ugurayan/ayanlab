package com.ayanlab.pms.backend.entity.tr;

import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.pms.backend.data.OrgScoped;
import com.ayanlab.pms.backend.data.enums.EnforcementCaseStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "EnforcementCase", schema = "ipms", indexes = @Index(name = "idx_ipms_enforcement_org", columnList = "organizationId"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnforcementCase extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @Column(name = "caseNumber")
    private String caseNumber;

    @NotBlank
    @Column(nullable = false, name = "ownerName")
    private String ownerName;

    @NotBlank
    @Column(nullable = false, name = "unitLabel")
    private String unitLabel;

    @Column(nullable = false, name = "amountCents")
    @Builder.Default
    private Long amountCents = 0L;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EnforcementCaseStatusEnum status = EnforcementCaseStatusEnum.OPEN;

    @Column(name = "openedAt")
    private Instant openedAt;

    @Column(columnDefinition = "text", name = "timelineJson")
    private String timelineJson;
}