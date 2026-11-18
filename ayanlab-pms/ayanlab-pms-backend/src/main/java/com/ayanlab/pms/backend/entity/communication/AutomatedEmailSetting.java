package com.ayanlab.pms.backend.entity.communication;

import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.pms.backend.data.OrgScoped;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
    name = "AutomatedEmailSetting",
    schema = "ipms",
    indexes = @Index(name = "idx_ipms_automatedemailsetting_org", columnList = "organizationId")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutomatedEmailSetting extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @NotBlank
    @Column(nullable = false, name = "recipientType")
    private String recipientType;

    @NotBlank
    @Column(nullable = false, name = "email")
    private String email;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "actionLabel")
    @Builder.Default
    private String actionLabel = "View";

    @Column(name = "status")
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "metadataJson", columnDefinition = "text")
    private String metadataJson;

}
