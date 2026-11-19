package com.ayanlab.pms.backend.entity.maintenance;

import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.pms.backend.data.OrgScoped;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "EquipmentAsset",
    schema = "ipms",
    indexes = {
        @Index(name = "idx_ipms_equipment_org", columnList = "organizationId"),
        @Index(name = "idx_ipms_equipment_property", columnList = "propertyId")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipmentAsset extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @Column(name = "propertyId")
    private UUID propertyId;

    @Column(name = "unitId")
    private UUID unitId;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(name = "serialNumber")
    private String serialNumber;

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "lastServiceAt")
    private Instant lastServiceAt;

    @Column(columnDefinition = "text")
    private String notes;
}