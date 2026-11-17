package com.ayanlabs.core.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
    name = "PolicyGroupPermission",
    schema = "core",
    uniqueConstraints = @UniqueConstraint(columnNames = {"policyGroupId", "permissionId"}),
    indexes = @Index(name = "idx_policy_group_perm_group", columnList = "policyGroupId")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyGroupPermission extends BaseEntity {

    @Column(nullable = false, name = "policyGroupId")
    private UUID policyGroupId;

    @Column(nullable = false, name = "permissionId")
    private UUID permissionId;
}
