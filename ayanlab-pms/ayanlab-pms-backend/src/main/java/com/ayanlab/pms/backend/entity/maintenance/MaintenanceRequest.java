package com.ayanlab.pms.backend.entity.maintenance;

import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.pms.backend.data.OrgScoped;
import com.ayanlab.pms.backend.data.enums.MaintenancePriorityEnum;
import com.ayanlab.pms.backend.data.enums.MaintenanceStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
    name = "MaintenanceRequest",
    schema = "ipms",
    indexes = {
        @Index(name = "idx_ipms_maint_req_org", columnList = "organizationId"),
        @Index(name = "idx_ipms_maint_req_unit", columnList = "unitId")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceRequest extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @Column(nullable = false, name = "propertyId")
    private UUID propertyId;

    @Column(name = "unitId")
    private UUID unitId;

    @Column(nullable = false, name = "reportedByProfileId")
    private UUID reportedByProfileId;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MaintenancePriorityEnum priority = MaintenancePriorityEnum.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MaintenanceStatusEnum status = MaintenanceStatusEnum.OPEN;

    @Column(name = "category")
    private String category;
}
