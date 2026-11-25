package com.ayanlab.pms.backend.entity.tr;

import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.pms.backend.dto.OrgScoped;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "PatrolCheckpoint",
    schema = "ipms",
    indexes = {
        @Index(name = "idx_ipms_patrol_checkpoint_org", columnList = "organizationId"),
        @Index(name = "idx_ipms_patrol_checkpoint_route", columnList = "routeId")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatrolCheckpoint extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @Column(nullable = false, name = "routeId")
    private UUID routeId;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(name = "sortOrder")
    @Builder.Default
    private Integer sortOrder = 0;

    private BigDecimal latitude;
    private BigDecimal longitude;

    @Column(name = "lastScanAt")
    private Instant lastScanAt;
}