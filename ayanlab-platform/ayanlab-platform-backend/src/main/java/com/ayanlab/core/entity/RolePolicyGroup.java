package com.ayanlab.core.entity;

import com.ayanlab.core.authz.AuthzChangeListener;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@EntityListeners(AuthzChangeListener.class)
@Table(
    name = "RolePolicyGroup",
    schema = "core",
    uniqueConstraints = @UniqueConstraint(columnNames = {"roleId", "policyGroupId"}),
    indexes = @Index(name = "idx_role_policy_group_role", columnList = "roleId")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolePolicyGroup extends BaseEntity {

    @Column(nullable = false, name = "roleId")
    private UUID roleId;

    @Column(nullable = false, name = "policyGroupId")
    private UUID policyGroupId;
}
