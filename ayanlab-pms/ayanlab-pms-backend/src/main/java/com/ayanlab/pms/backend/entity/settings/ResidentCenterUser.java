package com.ayanlab.pms.backend.entity.settings;

import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.pms.backend.dto.OrgScoped;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
    name = "ResidentCenterUser",
    schema = "ipms",
    indexes = @Index(name = "idx_ipms_residentcenteruser_org", columnList = "organizationId")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResidentCenterUser extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @Column(name = "firstName")
    private String firstName;

    @Column(name = "lastName")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "type")
    private String type;

    @Column(name = "propertyLabel")
    private String propertyLabel;

    @Column(name = "accountStatus")
    @Builder.Default
    private String accountStatus = "Not invited";

    @Column(name = "status")
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "metadataJson", columnDefinition = "text")
    private String metadataJson;

}
