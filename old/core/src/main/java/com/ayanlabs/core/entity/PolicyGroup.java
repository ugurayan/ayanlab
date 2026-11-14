package com.ayanlabs.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
    name = "PolicyGroup",
    schema = "core",
    uniqueConstraints = @UniqueConstraint(columnNames = {"appId", "code"}),
    indexes = @Index(name = "idx_policy_group_app", columnList = "appId")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyGroup extends BaseEntity {

    @Column(nullable = false, name = "appId")
    private UUID appId;

    @NotBlank
    @Column(nullable = false)
    private String code;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "text")
    private String description;
}
