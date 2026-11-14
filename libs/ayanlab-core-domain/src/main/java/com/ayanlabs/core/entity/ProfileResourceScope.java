package com.ayanlabs.core.entity;

import com.ayanlabs.core.data.enums.ResourceScopeTypeEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
    name = "ProfileResourceScope",
    schema = "core",
    uniqueConstraints = @UniqueConstraint(columnNames = {"profileId", "resourceType", "resourceId"}),
    indexes = {
        @Index(name = "idx_profile_scope_profile", columnList = "profileId"),
        @Index(name = "idx_profile_scope_org", columnList = "organizationId")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResourceScope extends BaseEntity {

    @Column(nullable = false, name = "profileId")
    private UUID profileId;

    @Column(name = "organizationId")
    private UUID organizationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "resourceType")
    private ResourceScopeTypeEnum resourceType;

    @Column(nullable = false, name = "resourceId")
    private UUID resourceId;
}
