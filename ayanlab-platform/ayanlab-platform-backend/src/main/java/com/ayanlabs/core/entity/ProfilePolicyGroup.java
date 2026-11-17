package com.ayanlabs.core.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "ProfilePolicyGroup",
    schema = "core",
    uniqueConstraints = @UniqueConstraint(columnNames = {"profileId", "policyGroupId"}),
    indexes = @Index(name = "idx_profile_policy_group_profile", columnList = "profileId")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfilePolicyGroup extends BaseEntity {

    @Column(nullable = false, name = "profileId")
    private UUID profileId;

    @Column(nullable = false, name = "policyGroupId")
    private UUID policyGroupId;

    @Column(name = "grantedAt")
    private Instant grantedAt;

    @Column(name = "grantedBy")
    private UUID grantedBy;

    @Column(name = "expiresAt")
    private Instant expiresAt;
}
