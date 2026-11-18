package com.ayanlab.pms.backend.entity.settings;

import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.pms.backend.data.OrgScoped;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
    name = "ResidentCenterContact",
    schema = "ipms",
    indexes = @Index(name = "idx_ipms_residentcentercontact_org", columnList = "organizationId")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResidentCenterContact extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @NotBlank
    @Column(nullable = false, name = "name")
    private String name;

    @Column(name = "category")
    private String category;

    @Column(name = "location")
    private String location;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "website")
    private String website;

    @Column(name = "status")
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "metadataJson", columnDefinition = "text")
    private String metadataJson;

}
