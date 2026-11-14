package com.ayanlabs.propertymgmt.entity.communication;

import com.ayanlabs.core.entity.BaseEntity;
import com.ayanlabs.propertymgmt.data.OrgScoped;
import com.ayanlabs.propertymgmt.data.enums.AnnouncementStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "Announcement",
    schema = "ipms",
    indexes = {
        @Index(name = "idx_ipms_announcement_org", columnList = "organizationId"),
        @Index(name = "idx_ipms_announcement_assoc", columnList = "associationId")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Announcement extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @Column(name = "associationId")
    private UUID associationId;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AnnouncementStatusEnum status = AnnouncementStatusEnum.DRAFT;

    @Column(name = "publishedAt")
    private Instant publishedAt;

    @Column(name = "expiresAt")
    private Instant expiresAt;

    @Column(nullable = false, name = "createdByProfileId")
    private UUID createdByProfileId;
}
