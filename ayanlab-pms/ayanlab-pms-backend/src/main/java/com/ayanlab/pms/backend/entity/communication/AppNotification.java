package com.ayanlab.pms.backend.entity.communication;

import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.pms.backend.data.OrgScoped;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
    name = "AppNotification",
    schema = "ipms",
    indexes = @Index(name = "idx_ipms_appnotification_org", columnList = "organizationId")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppNotification extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @NotBlank
    @Column(nullable = false, name = "title")
    private String title;

    @Column(columnDefinition = "text")
    private String body;

    @Column(name = "notificationType")
    private String notificationType;

    @Column(name = "readFlag")
    @Builder.Default
    private Boolean readFlag = false;

    @Column(name = "status")
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "metadataJson", columnDefinition = "text")
    private String metadataJson;

}
