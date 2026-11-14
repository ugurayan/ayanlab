package com.ayanlabs.propertymgmt.entity.communication;

import com.ayanlabs.core.entity.BaseEntity;
import com.ayanlabs.propertymgmt.data.OrgScoped;
import com.ayanlabs.propertymgmt.data.enums.CommunicationEmailStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "CommunicationEmail",
    schema = "ipms",
    indexes = @Index(name = "idx_ipms_email_org", columnList = "organizationId")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunicationEmail extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @NotBlank
    @Column(nullable = false, name = "recipientEmail")
    private String recipientEmail;

    @NotBlank
    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, columnDefinition = "text")
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CommunicationEmailStatusEnum status = CommunicationEmailStatusEnum.DRAFT;

    @Column(name = "sentAt")
    private Instant sentAt;

    @Column(name = "externalMessageId")
    private String externalMessageId;
}
