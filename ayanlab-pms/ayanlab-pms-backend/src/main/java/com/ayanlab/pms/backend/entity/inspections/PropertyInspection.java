package com.ayanlab.pms.backend.entity.inspections;

import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.pms.backend.dto.OrgScoped;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
    name = "PropertyInspection",
    schema = "ipms",
    indexes = @Index(name = "idx_ipms_propertyinspection_org", columnList = "organizationId")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyInspection extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @NotBlank
    @Column(nullable = false, name = "title")
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "status")
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "metadataJson", columnDefinition = "text")
    private String metadataJson;
}
