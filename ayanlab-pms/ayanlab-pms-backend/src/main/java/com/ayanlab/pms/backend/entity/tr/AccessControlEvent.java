package com.ayanlab.pms.backend.entity.tr;

import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.pms.backend.data.OrgScoped;
import com.ayanlab.pms.backend.data.enums.AccessControlEventTypeEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "AccessControlEvent", schema = "ipms", indexes = @Index(name = "idx_ipms_access_ctrl_org", columnList = "organizationId"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessControlEvent extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @NotBlank
    @Column(nullable = false, name = "plateOrCard")
    private String plateOrCard;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "eventType")
    private AccessControlEventTypeEnum eventType;

    @Column(nullable = false, name = "occurredAt")
    private Instant occurredAt;

    @Column(name = "gateName")
    private String gateName;
}