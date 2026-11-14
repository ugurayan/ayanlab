package com.ayanlabs.propertymgmt.entity.maintenance;

import com.ayanlabs.core.entity.BaseEntity;
import com.ayanlabs.propertymgmt.data.OrgScoped;
import com.ayanlabs.propertymgmt.data.enums.FieldStaffPresenceStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
    name = "FieldStaffPresence",
    schema = "ipms",
    indexes = {
        @Index(name = "idx_ipms_field_staff_org", columnList = "organizationId"),
        @Index(name = "idx_ipms_field_staff_profile", columnList = "profileId")
    },
    uniqueConstraints = @UniqueConstraint(columnNames = {"organizationId", "profileId"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldStaffPresence extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @Column(nullable = false, name = "profileId")
    private UUID profileId;

    @Column(name = "vendorId")
    private UUID vendorId;

    @Column(name = "displayName")
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private FieldStaffPresenceStatusEnum status = FieldStaffPresenceStatusEnum.AVAILABLE;

    @Column(columnDefinition = "text")
    private String notes;
}
